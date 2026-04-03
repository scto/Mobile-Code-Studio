package com.scto.mcs.termux.application.terminal

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.media.AudioManager
import android.view.Gravity
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.scto.mcs.termux.application.TermuxActivity
import com.scto.mcs.termux.application.models.UserAction
import com.scto.mcs.termux.application.terminal.io.KeyboardShortcut
import com.scto.mcs.termux.shared.logger.Logger
import com.scto.mcs.termux.shared.termux.extrakeys.SpecialButton
import com.scto.mcs.termux.shared.termux.settings.properties.TermuxPropertyConstants
import com.scto.mcs.termux.shared.termux.terminal.TermuxTerminalViewClientBase
import com.scto.mcs.termux.shared.view.KeyboardUtils
import com.scto.mcs.termux.shared.view.ViewUtils
import com.termux.R
import com.termux.terminal.KeyHandler
import com.termux.terminal.TerminalSession
import com.termux.view.TerminalView
import org.json.JSONException

class TermuxTerminalViewClient(
    private val mActivity: TermuxActivity,
    private val mTermuxTerminalSessionActivityClient: TermuxTerminalSessionActivityClient
) : TermuxTerminalViewClientBase() {

    var mVirtualControlKeyDown: Boolean = false
    var mVirtualFnKeyDown: Boolean = false

    private var mShowSoftKeyboardRunnable: Runnable? = null
    private var mShowSoftKeyboardIgnoreOnce: Boolean = false
    private var mShowSoftKeyboardWithDelayOnce: Boolean = false
    private var mTerminalCursorBlinkerStateAlreadySet: Boolean = false
    private var mSessionShortcuts: List<KeyboardShortcut>? = null

    companion object {
        private const val LOG_TAG = "TermuxTerminalViewClient"
    }

    fun onCreate() {
        onReloadProperties()
        mActivity.getTerminalView().setTextSize(mActivity.getPreferences()!!.getFontSize())
        mActivity.getTerminalView().setKeepScreenOn(mActivity.getPreferences()!!.shouldKeepScreenOn())
    }

    fun onStart() {
        val isTerminalViewKeyLoggingEnabled = mActivity.getPreferences()!!.isTerminalViewKeyLoggingEnabled()
        mActivity.getTerminalView().setIsTerminalViewKeyLoggingEnabled(isTerminalViewKeyLoggingEnabled)
        mActivity.getTermuxActivityRootView().setIsRootViewLoggingEnabled(isTerminalViewKeyLoggingEnabled)
        ViewUtils.setIsViewUtilsLoggingEnabled(isTerminalViewKeyLoggingEnabled)
    }

    fun onResume() {
        setSoftKeyboardState(true, mActivity.isActivityRecreated())
        mTerminalCursorBlinkerStateAlreadySet = false
        if (mActivity.getTerminalView().mEmulator != null) {
            setTerminalCursorBlinkerState(true)
            mTerminalCursorBlinkerStateAlreadySet = true
        }
    }

    fun onStop() {
        setTerminalCursorBlinkerState(false)
    }

    fun onReloadProperties() {
        setSessionShortcuts()
    }

    fun onReloadActivityStyling() {
        setSoftKeyboardState(false, true)
        setTerminalCursorBlinkerState(true)
    }

    override fun onEmulatorSet() {
        if (!mTerminalCursorBlinkerStateAlreadySet) {
            setTerminalCursorBlinkerState(true)
            mTerminalCursorBlinkerStateAlreadySet = true
        }
    }

    override fun onScale(scale: Float): Float {
        if (scale < 0.9f || scale > 1.1f) {
            changeFontSize(scale > 1.0f)
            return 1.0f
        }
        return scale
    }

    override fun onSingleTapUp(e: MotionEvent) {
        val term = mActivity.getCurrentSession()?.emulator ?: return
        if (mActivity.getProperties()!!.shouldOpenTerminalTranscriptURLOnClick()) {
            val columnAndRow = mActivity.getTerminalView().getColumnAndRow(e, true)
            val wordAtTap = term.screen.getWordAtLocation(columnAndRow[0], columnAndRow[1])
            val urlSet = com.scto.mcs.termux.shared.termux.data.TermuxUrlUtils.extractUrls(wordAtTap)
            if (!urlSet.isEmpty()) {
                com.scto.mcs.termux.shared.interact.ShareUtils.openUrl(mActivity, urlSet.iterator().next().toString())
                return
            }
        }
        if (!term.isMouseTrackingActive && !e.isFromSource(InputDevice.SOURCE_MOUSE)) {
            if (!KeyboardUtils.areDisableSoftKeyboardFlagsSet(mActivity))
                KeyboardUtils.showSoftKeyboard(mActivity, mActivity.getTerminalView())
        }
    }

    override fun shouldBackButtonBeMappedToEscape(): Boolean = mActivity.getProperties()!!.isBackKeyTheEscapeKey()
    override fun shouldEnforceCharBasedInput(): Boolean = mActivity.getProperties()!!.isEnforcingCharBasedInput()
    override fun shouldUseCtrlSpaceWorkaround(): Boolean = mActivity.getProperties()!!.isUsingCtrlSpaceWorkaround()
    override fun isTerminalViewSelected(): Boolean = mActivity.getTerminalToolbarViewPager() == null || mActivity.isTerminalViewSelected() || mActivity.getTerminalView().hasFocus()

    override fun copyModeChanged(copyMode: Boolean) {
        mActivity.getDrawer().setDrawerLockMode(if (copyMode) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    @SuppressLint("RtlHardcoded")
    override fun onKeyDown(keyCode: Int, e: KeyEvent, currentSession: TerminalSession): Boolean {
        if (handleVirtualKeys(keyCode, e, true)) return true
        if (keyCode == KeyEvent.KEYCODE_ENTER && !currentSession.isRunning) {
            mTermuxTerminalSessionActivityClient.removeFinishedSession(currentSession)
            return true
        } else if (!mActivity.getProperties()!!.areHardwareKeyboardShortcutsDisabled() && e.isCtrlPressed && e.isAltPressed) {
            val unicodeChar = e.getUnicodeChar(0)
            when {
                keyCode == KeyEvent.KEYCODE_DPAD_DOWN || unicodeChar == 'n'.code -> mTermuxTerminalSessionActivityClient.switchToSession(true)
                keyCode == KeyEvent.KEYCODE_DPAD_UP || unicodeChar == 'p'.code -> mTermuxTerminalSessionActivityClient.switchToSession(false)
                keyCode == KeyEvent.KEYCODE_DPAD_RIGHT -> mActivity.getDrawer().openDrawer(Gravity.LEFT)
                keyCode == KeyEvent.KEYCODE_DPAD_LEFT -> mActivity.getDrawer().closeDrawers()
                unicodeChar == 'k'.code -> onToggleSoftKeyboardRequest()
                unicodeChar == 'm'.code -> mActivity.getTerminalView().showContextMenu()
                unicodeChar == 'r'.code -> mTermuxTerminalSessionActivityClient.renameSession(currentSession)
                unicodeChar == 'c'.code -> mTermuxTerminalSessionActivityClient.addNewSession(false, null)
                unicodeChar == 'u'.code -> mActivity.getTermuxTerminalViewClient()!!.showUrlSelection()
                unicodeChar == 'v'.code -> mActivity.getTermuxTerminalViewClient()!!.doPaste()
                unicodeChar == '+'.code || e.getUnicodeChar(KeyEvent.META_SHIFT_ON) == '+'.code -> changeFontSize(true)
                unicodeChar == '-'.code -> changeFontSize(false)
                unicodeChar in '1'.code..'9'.code -> mTermuxTerminalSessionActivityClient.switchToSession(unicodeChar - '1'.code)
            }
            return true
        }
        return false
    }

    override fun onKeyUp(keyCode: Int, e: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mActivity.getTerminalView().mEmulator == null) {
            mActivity.finishActivityIfNotFinishing()
            return true
        }
        return handleVirtualKeys(keyCode, e, false)
    }

    private fun handleVirtualKeys(keyCode: Int, event: KeyEvent, down: Boolean): Boolean {
        val inputDevice = event.device
        return when {
            mActivity.getProperties()!!.areVirtualVolumeKeysDisabled() -> false
            inputDevice != null && inputDevice.keyboardType == InputDevice.KEYBOARD_TYPE_ALPHABETIC -> false
            keyCode == KeyEvent.KEYCODE_VOLUME_DOWN -> { mVirtualControlKeyDown = down; true }
            keyCode == KeyEvent.KEYCODE_VOLUME_UP -> { mVirtualFnKeyDown = down; true }
            else -> false
        }
    }

    override fun readControlKey(): Boolean = readExtraKeysSpecialButton(SpecialButton.CTRL) || mVirtualControlKeyDown
    override fun readAltKey(): Boolean = readExtraKeysSpecialButton(SpecialButton.ALT)
    override fun readShiftKey(): Boolean = readExtraKeysSpecialButton(SpecialButton.SHIFT)
    override fun readFnKey(): Boolean = readExtraKeysSpecialButton(SpecialButton.FN)

    private fun readExtraKeysSpecialButton(specialButton: SpecialButton): Boolean {
        val state = mActivity.getExtraKeysView()?.readSpecialButton(specialButton, true) ?: return false
        return state
    }

    override fun onCodePoint(codePoint: Int, ctrlDown: Boolean, session: TerminalSession): Boolean {
        if (mVirtualFnKeyDown) {
            var resultingKeyCode = -1
            var resultingCodePoint = -1
            var altDown = false
            val lowerCase = Character.toLowerCase(codePoint)
            when (lowerCase) {
                'w'.code -> resultingKeyCode = KeyEvent.KEYCODE_DPAD_UP
                'a'.code -> resultingKeyCode = KeyEvent.KEYCODE_DPAD_LEFT
                's'.code -> resultingKeyCode = KeyEvent.KEYCODE_DPAD_DOWN
                'd'.code -> resultingKeyCode = KeyEvent.KEYCODE_DPAD_RIGHT
                'p'.code -> resultingKeyCode = KeyEvent.KEYCODE_PAGE_UP
                'n'.code -> resultingKeyCode = KeyEvent.KEYCODE_PAGE_DOWN
                't'.code -> resultingKeyCode = KeyEvent.KEYCODE_TAB
                'i'.code -> resultingKeyCode = KeyEvent.KEYCODE_INSERT
                'h'.code -> resultingCodePoint = '~'.code
                'u'.code -> resultingCodePoint = '_'.code
                'l'.code -> resultingCodePoint = '|'.code
                in '1'.code..'9'.code -> resultingKeyCode = (codePoint - '1'.code) + KeyEvent.KEYCODE_F1
                '0'.code -> resultingKeyCode = KeyEvent.KEYCODE_F10
                'e'.code -> resultingCodePoint = 27
                '.'.code -> resultingCodePoint = 28
                'b'.code, 'f'.code, 'x'.code -> { resultingCodePoint = lowerCase; altDown = true }
                'v'.code -> {
                    val audio = mActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    audio.adjustSuggestedStreamVolume(AudioManager.ADJUST_SAME, AudioManager.USE_DEFAULT_STREAM_TYPE, AudioManager.FLAG_SHOW_UI)
                }
                'q'.code, 'k'.code -> {
                    mActivity.toggleTerminalToolbar()
                    mVirtualFnKeyDown = false
                }
            }
            if (resultingKeyCode != -1) {
                val term = session.emulator
                session.write(KeyHandler.getCode(resultingKeyCode, 0, term.isCursorKeysApplicationMode, term.isKeypadApplicationMode))
            } else if (resultingCodePoint != -1) {
                session.writeCodePoint(altDown, resultingCodePoint)
            }
            return true
        } else if (ctrlDown) {
            if (codePoint == 10 && !session.isRunning) {
                mTermuxTerminalSessionActivityClient.removeFinishedSession(session)
                return true
            }
            mSessionShortcuts?.let { shortcuts ->
                val codePointLowerCase = Character.toLowerCase(codePoint)
                for (shortcut in shortcuts.reversed()) {
                    if (codePointLowerCase == shortcut.codePoint) {
                        when (shortcut.shortcutAction) {
                            TermuxPropertyConstants.ACTION_SHORTCUT_CREATE_SESSION -> mTermuxTerminalSessionActivityClient.addNewSession(false, null)
                            TermuxPropertyConstants.ACTION_SHORTCUT_NEXT_SESSION -> mTermuxTerminalSessionActivityClient.switchToSession(true)
                            TermuxPropertyConstants.ACTION_SHORTCUT_PREVIOUS_SESSION -> mTermuxTerminalSessionActivityClient.switchToSession(false)
                            TermuxPropertyConstants.ACTION_SHORTCUT_RENAME_SESSION -> mTermuxTerminalSessionActivityClient.renameSession(mActivity.getCurrentSession())
                        }
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun setSessionShortcuts() {
        mSessionShortcuts = TermuxPropertyConstants.MAP_SESSION_SHORTCUTS.entries.mapNotNull { entry ->
            val codePoint = mActivity.getProperties()!!.getInternalPropertyValue(entry.key, true) as? Int
            codePoint?.let { KeyboardShortcut(it, entry.value) }
        }
    }

    private fun changeFontSize(increase: Boolean) {
        mActivity.getPreferences()!!.changeFontSize(increase)
        mActivity.getTerminalView().setTextSize(mActivity.getPreferences()!!.getFontSize())
    }

    fun onToggleSoftKeyboardRequest() {
        if (mActivity.getProperties()!!.shouldEnableDisableSoftKeyboardOnToggle()) {
            if (!KeyboardUtils.areDisableSoftKeyboardFlagsSet(mActivity)) {
                mActivity.getPreferences()!!.setSoftKeyboardEnabled(false)
                KeyboardUtils.disableSoftKeyboard(mActivity, mActivity.getTerminalView())
            } else {
                mActivity.getPreferences()!!.setSoftKeyboardEnabled(true)
                KeyboardUtils.clearDisableSoftKeyboardFlags(mActivity)
                if (mShowSoftKeyboardWithDelayOnce) {
                    mShowSoftKeyboardWithDelayOnce = false
                    mActivity.getTerminalView().postDelayed(getShowSoftKeyboardRunnable(), 500)
                    mActivity.getTerminalView().requestFocus()
                } else {
                    KeyboardUtils.showSoftKeyboard(mActivity, mActivity.getTerminalView())
                }
            }
        } else {
            if (!mActivity.getPreferences()!!.isSoftKeyboardEnabled) {
                KeyboardUtils.disableSoftKeyboard(mActivity, mActivity.getTerminalView())
            } else {
                KeyboardUtils.clearDisableSoftKeyboardFlags(mActivity)
                KeyboardUtils.toggleSoftKeyboard(mActivity)
            }
        }
    }

    fun setSoftKeyboardState(isStartup: Boolean, isReloadTermuxProperties: Boolean) {
        var noShowKeyboard = false
        if (KeyboardUtils.shouldSoftKeyboardBeDisabled(mActivity,
                mActivity.getPreferences()!!.isSoftKeyboardEnabled,
                mActivity.getPreferences()!!.isSoftKeyboardEnabledOnlyIfNoHardware)) {
            KeyboardUtils.disableSoftKeyboard(mActivity, mActivity.getTerminalView())
            mActivity.getTerminalView().requestFocus()
            noShowKeyboard = true
            if (isStartup && mActivity.isOnResumeAfterOnCreate()) mShowSoftKeyboardWithDelayOnce = true
        } else {
            KeyboardUtils.setSoftInputModeAdjustResize(mActivity)
            KeyboardUtils.clearDisableSoftKeyboardFlags(mActivity)
            if (isStartup && mActivity.getProperties()!!.shouldSoftKeyboardBeHiddenOnStartup()) {
                KeyboardUtils.setSoftKeyboardAlwaysHiddenFlags(mActivity)
                KeyboardUtils.hideSoftKeyboard(mActivity, mActivity.getTerminalView())
                mActivity.getTerminalView().requestFocus()
                noShowKeyboard = true
                mShowSoftKeyboardIgnoreOnce = true
            }
        }
        mActivity.getTerminalView().onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            val textInputView = mActivity.findViewById<EditText>(R.id.terminal_toolbar_text_input)
            val textInputViewHasFocus = textInputView?.hasFocus() ?: false
            if (hasFocus || textInputViewHasFocus) {
                if (mShowSoftKeyboardIgnoreOnce) {
                    mShowSoftKeyboardIgnoreOnce = false
                    return@OnFocusChangeListener
                }
            }
            KeyboardUtils.setSoftKeyboardVisibility(getShowSoftKeyboardRunnable(), mActivity, mActivity.getTerminalView(), hasFocus || textInputViewHasFocus)
        }
        if (!isReloadTermuxProperties && !noShowKeyboard) {
            mActivity.getTerminalView().requestFocus()
            mActivity.getTerminalView().postDelayed(getShowSoftKeyboardRunnable(), 300)
        }
    }

    private fun getShowSoftKeyboardRunnable(): Runnable {
        if (mShowSoftKeyboardRunnable == null) {
            mShowSoftKeyboardRunnable = Runnable { KeyboardUtils.showSoftKeyboard(mActivity, mActivity.getTerminalView()) }
        }
        return mShowSoftKeyboardRunnable!!
    }

    fun setTerminalCursorBlinkerState(start: Boolean) {
        if (start) {
            if (mActivity.getTerminalView().setTerminalCursorBlinkerRate(mActivity.getProperties()!!.getTerminalCursorBlinkRate()))
                mActivity.getTerminalView().setTerminalCursorBlinkerState(true, true)
        } else {
            mActivity.getTerminalView().setTerminalCursorBlinkerState(false, true)
        }
    }

    fun shareSessionTranscript() {
        val session = mActivity.getCurrentSession() ?: return
        var transcriptText = com.scto.mcs.termux.shared.shell.ShellUtils.getTerminalSessionTranscriptText(session, false, true) ?: return
        transcriptText = com.scto.mcs.termux.shared.data.DataUtils.getTruncatedCommandOutput(transcriptText, com.scto.mcs.termux.shared.data.DataUtils.TRANSACTION_SIZE_LIMIT_IN_BYTES, false, true, false)!!.trim()
        com.scto.mcs.termux.shared.interact.ShareUtils.shareText(mActivity, mActivity.getString(R.string.title_share_transcript), transcriptText, mActivity.getString(R.string.title_share_transcript_with))
    }

    fun shareSelectedText() {
        val selectedText = mActivity.getTerminalView().storedSelectedText
        if (com.scto.mcs.termux.shared.data.DataUtils.isNullOrEmpty(selectedText)) return
        com.scto.mcs.termux.shared.interact.ShareUtils.shareText(mActivity, mActivity.getString(R.string.title_share_selected_text), selectedText, mActivity.getString(R.string.title_share_selected_text_with))
    }

    fun showUrlSelection() {
        val session = mActivity.getCurrentSession() ?: return
        val text = com.scto.mcs.termux.shared.shell.ShellUtils.getTerminalSessionTranscriptText(session, true, true)
        val urlSet = com.scto.mcs.termux.shared.termux.data.TermuxUrlUtils.extractUrls(text)
        if (urlSet.isEmpty()) {
            AlertDialog.Builder(mActivity).setMessage(R.string.title_select_url_none_found).show()
            return
        }
        val urls = urlSet.toTypedArray()
        urls.reverse()
        val dialog = AlertDialog.Builder(mActivity).setItems(urls) { di, which ->
            val url = urls[which].toString()
            com.scto.mcs.termux.shared.interact.ShareUtils.copyTextToClipboard(mActivity, url, mActivity.getString(R.string.msg_select_url_copied_to_clipboard))
        }.setTitle(R.string.title_select_url_dialog).create()
        dialog.setOnShowListener { di ->
            val lv = dialog.listView
            lv.setOnItemLongClickListener { parent, view, position, id ->
                dialog.dismiss()
                com.scto.mcs.termux.shared.interact.ShareUtils.openUrl(mActivity, urls[position].toString())
                true
            }
        }
        dialog.show()
    }

    fun reportIssueFromTranscript() {
        val session = mActivity.getCurrentSession() ?: return
        val transcriptText = com.scto.mcs.termux.shared.shell.ShellUtils.getTerminalSessionTranscriptText(session, false, true) ?: return
        com.scto.mcs.termux.shared.interact.MessageDialogUtils.showMessage(mActivity, TermuxConstants.TERMUX_APP_NAME + " Report Issue",
            mActivity.getString(R.string.msg_add_termux_debug_info),
            mActivity.getString(R.string.action_yes), { dialog, which -> reportIssueFromTranscript(transcriptText, true) },
            mActivity.getString(R.string.action_no), { dialog, which -> reportIssueFromTranscript(transcriptText, false) },
            null)
    }

    private fun reportIssueFromTranscript(transcriptText: String, addTermuxDebugInfo: Boolean) {
        Logger.showToast(mActivity, mActivity.getString(R.string.msg_generating_report), true)
        Thread {
            val reportString = StringBuilder()
            val title = TermuxConstants.TERMUX_APP_NAME + " Report Issue"
            reportString.append("## Transcript\n")
            reportString.append("\n").append(com.scto.mcs.termux.shared.markdown.MarkdownUtils.getMarkdownCodeForString(transcriptText, true))
            reportString.append("\n##\n")
            if (addTermuxDebugInfo) {
                reportString.append("\n\n").append(com.scto.mcs.termux.shared.termux.TermuxUtils.getAppInfoMarkdownString(mActivity, com.scto.mcs.termux.shared.termux.TermuxUtils.AppInfoMode.TERMUX_AND_PLUGIN_PACKAGES))
            } else {
                reportString.append("\n\n").append(com.scto.mcs.termux.shared.termux.TermuxUtils.getAppInfoMarkdownString(mActivity, com.scto.mcs.termux.shared.termux.TermuxUtils.AppInfoMode.TERMUX_PACKAGE))
            }
            reportString.append("\n\n").append(com.scto.mcs.termux.shared.android.AndroidUtils.getDeviceInfoMarkdownString(mActivity, true))
            if (com.scto.mcs.termux.shared.termux.TermuxBootstrap.isAppPackageManagerAPT()) {
                val termuxAptInfo = com.scto.mcs.termux.shared.termux.TermuxUtils.geAPTInfoMarkdownString(mActivity)
                if (termuxAptInfo != null) reportString.append("\n\n").append(termuxAptInfo)
            }
            if (addTermuxDebugInfo) {
                val termuxDebugInfo = com.scto.mcs.termux.shared.termux.TermuxUtils.getTermuxDebugMarkdownString(mActivity)
                if (termuxDebugInfo != null) reportString.append("\n\n").append(termuxDebugInfo)
            }
            val userActionName = UserAction.REPORT_ISSUE_FROM_TRANSCRIPT.getName()
            val reportInfo = com.scto.mcs.termux.shared.models.ReportInfo(userActionName,
                TermuxConstants.TERMUX_APP.TERMUX_ACTIVITY_NAME, title)
            reportInfo.reportString = reportString.toString()
            reportInfo.reportStringSuffix = "\n\n" + com.scto.mcs.termux.shared.termux.TermuxUtils.getReportIssueMarkdownString(mActivity)
            reportInfo.setReportSaveFileLabelAndPath(userActionName,
                Environment.getExternalStorageDirectory().toString() + "/" +
                    com.scto.mcs.termux.shared.file.FileUtils.sanitizeFileName(TermuxConstants.TERMUX_APP_NAME + "-" + userActionName + ".log", true, true))
            com.scto.mcs.termux.shared.activities.ReportActivity.startReportActivity(mActivity, reportInfo)
        }.start()
    }

    fun doPaste() {
        val session = mActivity.getCurrentSession() ?: return
        if (!session.isRunning) return
        val text = com.scto.mcs.termux.shared.interact.ShareUtils.getTextStringFromClipboardIfSet(mActivity, true)
        if (text != null) session.emulator.paste(text)
    }
}
