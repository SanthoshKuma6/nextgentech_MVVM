package com.nextgenapp.nextgentech.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.nextgenapp.nextgentech.R

class PrefSessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    
    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_NAME = "user_name"
    }

    /**
     * Function to save user details
     */
    fun saveUserData(token: String, userName: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putString(USER_NAME, userName)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }


    /**
     * Get the user details
     */
    fun getUserDetails(): HashMap<String, String>{
        val user = HashMap<String, String>()
        user[USER_TOKEN] = prefs.getString(USER_TOKEN, null).toString()
        user[USER_NAME] = prefs.getString(USER_NAME, null).toString()
        return user
    }
}
