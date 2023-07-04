package com.nextgenapp.nextgentech.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nextgenapp.nextgentech.ui.main.manager.DialogMgr
import com.nextgenapp.nextgentech.ui.main.manager.MessageSheet

/**
 * Base Fragment - This class acts as a common class when extended to any fragment to perform common operations such as throw error, alert users,
 * launch activity etc....
 */

open class BaseFragment:  Fragment(), DialogMgr {

    private var messageSheet: MessageSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * setStatusBar - to change the status bar color pass the color value to the function
     */

    fun setStatusBar(color: Int) {
        try {
            val window: Window = requireActivity().window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireActivity(), color)
        }catch (e: java.lang.Exception){e.printStackTrace()}
    }

    /**
     * Launches activity with preferred bundle(share data from one screen to another) &
     * isClearPreviousTask (true - clear the backstack and open the activity on top stack ||
     * false - open activity over the current stack without clearing the backstack)
     */
    fun launchActivity(
        javaClass: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        isClearPreviousTask: Boolean = false
    ) {
        Intent(requireContext(), javaClass).apply {
            if (bundle != null)
                putExtras(bundle)

            if (isClearPreviousTask)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(this)
        }
    }

    /**
     * Error response from the api to alert the user
     */
    open val errorObserver = Observer<String> {
        showErrorMessage(it)
    }

    /**
     * Shows error message alert to the user.
     */
    fun showErrorMessage(message: String) {
        messageSheet?.dismiss()
        messageSheet = MessageSheet(
            this,
            message
        )
        messageSheet?.isCancelable = false
        messageSheet?.show(parentFragmentManager, messageSheet?.tag)
    }

    override fun onPositiveButtonClicked() {
        messageSheet?.dismiss()
    }
}