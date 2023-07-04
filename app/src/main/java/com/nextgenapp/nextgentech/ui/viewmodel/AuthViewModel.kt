package com.nextgenapp.nextgentech.ui.viewmodel

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.nextgenapp.nextgentech.data.repo.Repository
import com.nextgenapp.nextgentech.utils.isEmailValid
import com.nextgenapp.nextgentech.utils.showToast

class AuthViewModel(): ViewModel() {

    /**
     * ViewModel - The ViewModel class is a business logic or screen level state holder.
     * It exposes state to the UI and encapsulates related business logic.
     */

    //repository contains the api call functions
    private val repository by lazy { Repository() }

    //initialize the live data in viewmodel and will be accessed from activity
    val loginResponseLiveData by lazy { repository.loginLiveData }

    //error handler liveData
    val errorHandlerLiveData : MutableLiveData<String> = MutableLiveData()

    /**
     * validate the parameters need to make the login server calls.
     */
    suspend fun login(emailAddress: String, password: String){
        when{
            TextUtils.isEmpty(emailAddress) -> errorHandlerLiveData.value = "Email address can't be empty"
            !isEmailValid(emailAddress) -> errorHandlerLiveData.value = "Enter a valid email address"
            TextUtils.isEmpty(password) -> errorHandlerLiveData.value = "Password can't be empty"
            else -> {
                val jsonObject = JsonObject().apply {
                    addProperty("email", emailAddress)
                    addProperty("password", password)
                }
                //send the parameters to the repository
                repository.loginApi(jsonObject)
            }
        }
    }
}