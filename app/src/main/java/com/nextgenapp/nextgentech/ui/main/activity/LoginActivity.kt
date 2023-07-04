package com.nextgenapp.nextgentech.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.nextgenapp.nextgentech.R
import com.nextgenapp.nextgentech.data.api.Response
import com.nextgenapp.nextgentech.data.model.LoginResponse
import com.nextgenapp.nextgentech.data.pref.PrefSessionManager
import com.nextgenapp.nextgentech.databinding.ActivityLoginBinding
import com.nextgenapp.nextgentech.ui.base.BaseActivity
import com.nextgenapp.nextgentech.ui.main.bottomnav.BottomNavActivity
import com.nextgenapp.nextgentech.ui.viewmodel.AuthViewModel
import com.nextgenapp.nextgentech.utils.showHidePassword
import com.nextgenapp.nextgentech.utils.showLog
import com.nextgenapp.nextgentech.utils.showToast
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var activityLoginBinding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)
        setStatusBar(R.color.status_bar)
        activityLoginBinding.loginButton.setOnClickListener(this)
        activityLoginBinding.passwordToggle.setOnClickListener(this)

        authViewModel.loginResponseLiveData.observe(this, loginObserver)
        authViewModel.errorHandlerLiveData.observe(this, errorObserver)

//        activityLoginBinding.emailAddressEditText.setText("admin@gmail.com")
//        activityLoginBinding.passwordEdittext.setText("Admin@2022")

        val intent= Intent(this,BottomNavActivity::class.java)
        startActivity(intent)


    }

    private val loginObserver = Observer<Response<LoginResponse>> {
        when (it) {
            is Response.Success -> {
                if (it.data != null) {
                    if (it.data.data != null) {
                        val userData = it.data.data
                        val token = userData.token
                        val name = userData.name
                        if (token != null) {
                            PrefSessionManager(context = this).saveUserData(
                                token = token, userName = name!!
                            )
                            launchActivity(
                                javaClass = BottomNavActivity::class.java,
                                isClearPreviousTask = true
                            )
                        }
                    }
                }
            }
            is Response.Error -> {
                val loginData = it.data
                if (loginData != null) {
                    val responseData = loginData.data
                    if (responseData != null) {
                        showToast("FAILED : " + responseData.message)
                    } else {
                        showToast("FAILED : " + it.errorMessage)
                    }
                } else {
                    showToast("FAILED : " + it.errorMessage)
                }
            }
            is Response.Loading -> {

            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            activityLoginBinding.loginButton.id -> validateLogin()
            activityLoginBinding.passwordToggle.id -> showHidePassword(
                editText = activityLoginBinding.passwordEdittext,
                imageButton = activityLoginBinding.passwordToggle
            )
        }
    }

    private fun validateLogin() {
        proceedLogin(
            emailAddress = activityLoginBinding.emailAddressEditText.text.toString(),
            password = activityLoginBinding.passwordEdittext.text.toString()
        )
    }

    private fun proceedLogin(emailAddress: String, password: String) {
        lifecycleScope.launch {
            authViewModel.login(
                emailAddress = emailAddress, password = password
            )
        }
    }
}