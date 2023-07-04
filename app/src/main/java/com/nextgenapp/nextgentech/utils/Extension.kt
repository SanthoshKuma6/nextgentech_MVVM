package com.nextgenapp.nextgentech.utils

import android.app.Activity
import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import com.nextgenapp.nextgentech.R
import java.lang.reflect.Method


/**
 * Extension kotlin class is used to declare function that are commonly used through out the project.
 */

//To check the email address is valid or not
private val EMAIL_REGEX = "[A-Z0-9a-z.-_]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,3}"
fun isEmailValid(email: String): Boolean = EMAIL_REGEX.toRegex().matches(email)

//returns the toast message
fun Context.showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//returns the log
fun Context.showLog(message: String) = Log.d("nextgentech_log", message)

//hides the spinner dialog since we have used the custom spinner.
fun hideSpinnerDropDown(spinner: Spinner?) {
    try {
        val method: Method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
        method.isAccessible = true
        method.invoke(spinner)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
}
//used to hide the password visible toggle.
fun showHidePassword(editText: EditText, imageButton: ImageButton) {
    try {
        if (editText.transformationMethod.equals(
                PasswordTransformationMethod.getInstance()
            )
        ) {
            imageButton.setImageResource(R.drawable.hide_password);

            //Show Password
            editText.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

        } else {
            imageButton.setImageResource(R.drawable.view_password);

            //Hide Password
            editText.transformationMethod =
                PasswordTransformationMethod.getInstance();

        }
        editText.setSelection(editText.text.toString().length)
    }catch (e: java.lang.Exception){
        e.printStackTrace()
    }
}