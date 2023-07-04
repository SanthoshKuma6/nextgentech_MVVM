package com.nextgenapp.nextgentech.ui.base

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.nextgenapp.nextgentech.R
import com.nextgenapp.nextgentech.ui.main.manager.DialogMgr
import com.nextgenapp.nextgentech.ui.main.manager.MessageSheet
import com.nextgenapp.nextgentech.utils.showToast

/**
 * Base Activity - This class acts as a common class when extended to an Activity to perform common operations such as throw error, alert users,
 * launch activity etc....
 */
@SuppressLint("InflateParams")
open class BaseActivity : AppCompatActivity(), DialogMgr {

    private var messageSheet: MessageSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    /**
     * setStatusBar - to change the status bar color pass the color value to the function
     */

    fun setStatusBar(color: Int) {
        try {
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, color)
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
        Intent(this, javaClass).apply {
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
        showToast(it)
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
        messageSheet?.show(supportFragmentManager, messageSheet?.tag)
    }

    override fun onPositiveButtonClicked() {
        messageSheet?.dismiss()
    }
}