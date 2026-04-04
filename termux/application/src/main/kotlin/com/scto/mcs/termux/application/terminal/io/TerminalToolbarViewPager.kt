package com.scto.mcs.termux.application.terminal.io

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.termux.R
import com.scto.mcs.termux.application.TermuxActivity
import com.scto.mcs.termux.shared.termux.extrakeys.ExtraKeysView
import com.termux.terminal.TerminalSession

class TerminalToolbarViewPager {

    class PageAdapter(
        private val mActivity: TermuxActivity,
        private var mSavedTextInput: String?
    ) : PagerAdapter() {

        override fun getCount(): Int = 2

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(mActivity)
            val layout: View
            if (position == 0) {
                layout = inflater.inflate(R.layout.view_terminal_toolbar_extra_keys, collection, false)
                val extraKeysView = layout as ExtraKeysView
                extraKeysView.setExtraKeysViewClient(mActivity.getTermuxTerminalExtraKeys())
                extraKeysView.setButtonTextAllCaps(mActivity.getProperties()!!.shouldExtraKeysTextBeAllCaps())
                mActivity.setExtraKeysView(extraKeysView)
                extraKeysView.reload(mActivity.getTermuxTerminalExtraKeys()?.getExtraKeysInfo(),
                    mActivity.getTerminalToolbarDefaultHeight())

                // apply extra keys fix if enabled in prefs
                if (mActivity.getProperties()!!.isUsingFullScreen && mActivity.getProperties()!!.isUsingFullScreenWorkAround) {
                    FullScreenWorkAround.apply(mActivity)
                }

            } else {
                layout = inflater.inflate(R.layout.view_terminal_toolbar_text_input, collection, false)
                val editText = layout.findViewById<EditText>(R.id.terminal_toolbar_text_input)

                if (mSavedTextInput != null) {
                    editText.setText(mSavedTextInput)
                    mSavedTextInput = null
                }

                editText.setOnEditorActionListener { v, actionId, event ->
                    val session = mActivity.getCurrentSession()
                    if (session != null) {
                        if (session.isRunning) {
                            var textToSend = editText.text.toString()
                            if (textToSend.isEmpty()) textToSend = "\r"
                            session.write(textToSend)
                        } else {
                            mActivity.getTermuxTerminalSessionClient()?.removeFinishedSession(session)
                        }
                        editText.setText("")
                    }
                    true
                }
            }
            collection.addView(layout)
            return layout
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }
    }

    class OnPageChangeListener(
        private val mActivity: TermuxActivity,
        private val mTerminalToolbarViewPager: ViewPager
    ) : ViewPager.SimpleOnPageChangeListener() {

        override fun onPageSelected(position: Int) {
            if (position == 0) {
                mActivity.getTerminalView().requestFocus()
            } else {
                val editText = mTerminalToolbarViewPager.findViewById<EditText>(R.id.terminal_toolbar_text_input)
                editText?.requestFocus()
            }
        }
    }
}
