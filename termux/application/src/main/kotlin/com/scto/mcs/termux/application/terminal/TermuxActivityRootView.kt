package com.scto.mcs.termux.application.terminal

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.WindowInsetsCompat
import com.scto.mcs.termux.application.TermuxActivity
import com.scto.mcs.termux.shared.logger.Logger
import com.scto.mcs.termux.shared.view.ViewUtils

class TermuxActivityRootView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener {

    var mActivity: TermuxActivity? = null
    var marginBottom: Int? = null
    var lastMarginBottom: Int? = null
    var lastMarginBottomTime: Long = 0
    var lastMarginBottomExtraTime: Long = 0

    /** Log root view events. */
    private var ROOT_VIEW_LOGGING_ENABLED = false

    companion object {
        private const val LOG_TAG = "TermuxActivityRootView"
        private var mStatusBarHeight: Int = 0
    }

    fun setActivity(activity: TermuxActivity) {
        mActivity = activity
    }

    /**
     * Sets whether root view logging is enabled or not.
     *
     * @param value The boolean value that defines the state.
     */
    fun setIsRootViewLoggingEnabled(value: Boolean) {
        ROOT_VIEW_LOGGING_ENABLED = value
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (marginBottom != null) {
            if (ROOT_VIEW_LOGGING_ENABLED)
                Logger.logVerbose(LOG_TAG, "onMeasure: Setting bottom margin to $marginBottom")
            val params = layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, 0, 0, marginBottom!!)
            layoutParams = params
            marginBottom = null
            requestLayout()
        }
    }

    override fun onGlobalLayout() {
        if (mActivity == null || !mActivity!!.isVisible()) return

        val bottomSpaceView = mActivity!!.getTermuxActivityBottomSpaceView() ?: return

        val root_view_logging_enabled = ROOT_VIEW_LOGGING_ENABLED

        if (root_view_logging_enabled)
            Logger.logVerbose(LOG_TAG, ":\nonGlobalLayout:")

        val params = layoutParams as FrameLayout.LayoutParams

        // Get the position Rects of the bottom space view and the main window holding it
        val windowAndViewRects = ViewUtils.getWindowAndViewRects(bottomSpaceView, mStatusBarHeight)
        if (windowAndViewRects == null)
            return

        val windowAvailableRect = windowAndViewRects[0]
        val bottomSpaceViewRect = windowAndViewRects[1]

        val isVisible = ViewUtils.isRectAbove(windowAvailableRect, bottomSpaceViewRect)
        val isVisibleBecauseMargin = (windowAvailableRect.bottom == bottomSpaceViewRect.bottom) && params.bottomMargin > 0
        val isVisibleBecauseExtraMargin = ((bottomSpaceViewRect.bottom - windowAvailableRect.bottom) < 0)

        if (root_view_logging_enabled) {
            Logger.logVerbose(LOG_TAG, "windowAvailableRect " + ViewUtils.toRectString(windowAvailableRect) + ", bottomSpaceViewRect " + ViewUtils.toRectString(bottomSpaceViewRect))
            Logger.logVerbose(LOG_TAG, "windowAvailableRect.bottom " + windowAvailableRect.bottom +
                ", bottomSpaceViewRect.bottom " + bottomSpaceViewRect.bottom +
                ", diff " + (bottomSpaceViewRect.bottom - windowAvailableRect.bottom) + ", bottom " + params.bottomMargin +
                ", isVisible " + windowAvailableRect.contains(bottomSpaceViewRect) + ", isRectAbove " + ViewUtils.isRectAbove(windowAvailableRect, bottomSpaceViewRect) +
                ", isVisibleBecauseMargin " + isVisibleBecauseMargin + ", isVisibleBecauseExtraMargin " + isVisibleBecauseExtraMargin)
        }

        if (isVisible) {
            if (isVisibleBecauseMargin) {
                if (root_view_logging_enabled)
                    Logger.logVerbose(LOG_TAG, "Visible due to margin")

                if ((System.currentTimeMillis() - lastMarginBottomTime) > 40) {
                    lastMarginBottomTime = System.currentTimeMillis()
                    marginBottom = 0
                } else {
                    if (root_view_logging_enabled)
                        Logger.logVerbose(LOG_TAG, "Ignoring restoring marginBottom to 0 since called to quickly")
                }

                return
            }

            var setMargin = params.bottomMargin != 0

            if (isVisibleBecauseExtraMargin) {
                if ((System.currentTimeMillis() - lastMarginBottomExtraTime) > 40) {
                    if (root_view_logging_enabled)
                        Logger.logVerbose(LOG_TAG, "Resetting margin since visible due to extra margin")
                    lastMarginBottomExtraTime = System.currentTimeMillis()
                    lastMarginBottom = null
                    setMargin = true
                } else {
                    if (root_view_logging_enabled)
                        Logger.logVerbose(LOG_TAG, "Ignoring resetting margin since visible due to extra margin since called to quickly")
                }
            }

            if (setMargin) {
                if (root_view_logging_enabled)
                    Logger.logVerbose(LOG_TAG, "Setting bottom margin to 0")
                params.setMargins(0, 0, 0, 0)
                layoutParams = params
            } else {
                if (root_view_logging_enabled)
                    Logger.logVerbose(LOG_TAG, "Bottom margin already equals 0")
                marginBottom = lastMarginBottom
            }
        }
        else {
            var pxHidden = bottomSpaceViewRect.bottom - windowAvailableRect.bottom

            if (root_view_logging_enabled)
                Logger.logVerbose(LOG_TAG, "pxHidden $pxHidden, bottom ${params.bottomMargin}")

            var setMargin = params.bottomMargin != pxHidden

            if (pxHidden > 0 && params.bottomMargin > 0) {
                if (pxHidden != params.bottomMargin) {
                    if (root_view_logging_enabled)
                        Logger.logVerbose(LOG_TAG, "Force setting margin to 0 since not visible due to wrong margin")
                    pxHidden = 0
                } else {
                    if (root_view_logging_enabled)
                        Logger.logVerbose(LOG_TAG, "Force setting margin since not visible despite required margin")
                }
                setMargin = true
            }

            if (pxHidden < 0) {
                if (root_view_logging_enabled)
                    Logger.logVerbose(LOG_TAG, "Force setting margin to 0 since new margin is negative")
                pxHidden = 0
            }


            if (setMargin) {
                if (root_view_logging_enabled)
                    Logger.logVerbose(LOG_TAG, "Setting bottom margin to $pxHidden")
                params.setMargins(0, 0, 0, pxHidden)
                layoutParams = params
                lastMarginBottom = pxHidden
            } else {
                if (root_view_logging_enabled)
                    Logger.logVerbose(LOG_TAG, "Bottom margin already equals $pxHidden")
            }
        }
    }

    class WindowInsetsListener : View.OnApplyWindowInsetsListener {
        override fun onApplyWindowInsets(v: View, insets: WindowInsets): WindowInsets {
            mStatusBarHeight = WindowInsetsCompat.toWindowInsetsCompat(insets).getInsets(WindowInsetsCompat.Type.statusBars()).top
            return v.onApplyWindowInsets(insets)
        }
    }
}
