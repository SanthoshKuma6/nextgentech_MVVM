package com.nextgenapp.nextgentech.data.repo

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.nextgenapp.nextgentech.data.api.ApiInterface
import com.nextgenapp.nextgentech.data.api.Response
import com.nextgenapp.nextgentech.data.api.RetrofitApi
import com.nextgenapp.nextgentech.data.model.*
import com.nextgenapp.nextgentech.data.pref.PrefSessionManager
import com.nextgenapp.nextgentech.ui.base.NextGenClass
import com.nextgenapp.nextgentech.utils.NetworkUtils
import com.nextgenapp.nextgentech.utils.showLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject


class Repository {

    /**
     * NextGenClass.getContext - capture the context from the application class (application class
     * is called during the startup)
     */
    private val context by lazy { NextGenClass.getContext }

    /**
     * initialise the session manager
     */
    private val sessionManager = PrefSessionManager(context)

    /**
     * serverApi - Initialise the retrofit builder to handle the api calls
     */
    private val serverApi by lazy {
        RetrofitApi.retrofitInstance(context = context).create(ApiInterface::class.java)
    }

    /**
     * MutableLiveData - Pojo objects are initialised here to add the value from the api response .
     * Since it is MutableLiveData, the added values will be observed in the activity or fragment
     * where the observer is defined.
     */

    val loginLiveData = MutableLiveData<Response<LoginResponse>>()
    val employeeListResponse = MutableLiveData<Response<EmployeeListResponse>>()
    val employeeResponse = MutableLiveData<Response<EmployeeResponse>>()
    val departmentResponse = MutableLiveData<Response<DepartmentResponse>>()
    val createEmployeeResponse = MutableLiveData<Response<EmployeeCreateResponse>>()

    suspend fun loginApi(jsonObject: JsonObject) {
        // Check for internet connection availability
        if (NetworkUtils.isInternetAvailable(context = context)) {
            //activate the loader (progress dialog)
            loginLiveData.value = Response.Loading(showLoader = true)
            try {
                //trigger the login api call and get the result
                val result = serverApi.login(jsonObject = jsonObject)
                if (result.body() != null) {
                    //add the success response to the livedata
                    loginLiveData.value = Response.Success(data = result.body())
                    loginLiveData.value = Response.Loading(showLoader = false)
                } else {
                    //add the error response to the livedata
                    try {
                        loginLiveData.value =
                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    } catch (e: JSONException) {
                        loginLiveData.value = Response.Error(errorMessage = result.message())
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                //add the error response to the livedata
                loginLiveData.value = Response.Error(errorMessage = e.message.toString())
                loginLiveData.value = Response.Loading(showLoader = false)
            }
        } else {
            //add the error response to the livedata
            loginLiveData.value = Response.Error(errorMessage = "No Internet Connection")
        }
    }

    suspend fun getEmployeeList() {
        // Check for internet connection availability
        if (NetworkUtils.isInternetAvailable(context = context)) {
            //activate the loader (progress dialog)
            employeeListResponse.value = Response.Loading(showLoader = true)
            try {
                //get access token
                val accessToken = sessionManager.fetchAuthToken()

                //trigger the login api call and get the result
                val result = serverApi.getEmployeeList(authHeader = "Bearer $accessToken")
                if (result.body() != null) {
                    //add the success response to the livedata
                    employeeListResponse.value = Response.Success(data = result.body())
                    employeeListResponse.value = Response.Loading(showLoader = false)
                } else {
                    //add the error response to the livedata
                    try {
                        employeeListResponse.value =
                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    } catch (e: JSONException) {
                        employeeListResponse.value = Response.Error(errorMessage = result.message())
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                //add the error response to the livedata
                employeeListResponse.value = Response.Error(errorMessage = e.message.toString())
                employeeListResponse.value = Response.Loading(showLoader = false)
            }
        } else {
            //add the error response to the livedata
            employeeListResponse.value = Response.Error(errorMessage = "No Internet Connection")
        }
    }

    suspend fun getEmployee(employeeId: String) {
        // Check for internet connection availability
        if (NetworkUtils.isInternetAvailable(context = context)) {
            //activate the loader (progress dialog)
            employeeResponse.value = Response.Loading(showLoader = true)
            try {
                //get access token
                val accessToken = sessionManager.fetchAuthToken()

                //trigger the login api call and get the result
                val result = serverApi.getEmployee(
                    employeeId = employeeId,
                    authHeader = "Bearer $accessToken"
                )
                if (result.body() != null) {
                    //add the success response to the livedata
                    employeeResponse.value = Response.Success(data = result.body())
                    employeeResponse.value = Response.Loading(showLoader = false)
                } else {
                    //add the error response to the livedata
                    try {
                        employeeResponse.value =
                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    } catch (e: JSONException) {
                        employeeResponse.value = Response.Error(errorMessage = result.message())
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                //add the error response to the livedata
                employeeResponse.value = Response.Error(errorMessage = e.message.toString())
                employeeResponse.value = Response.Loading(showLoader = false)
            }
        } else {
            //add the error response to the livedata
            employeeResponse.value = Response.Error(errorMessage = "No Internet Connection")
        }
    }

    suspend fun getDepartment() {
        // Check for internet connection availability
        if (NetworkUtils.isInternetAvailable(context = context)) {
            //activate the loader (progress dialog)
            departmentResponse.value = Response.Loading(showLoader = true)
            try {
                //get access token
                val accessToken = sessionManager.fetchAuthToken()

                //trigger the login api call and get the result
                val result = serverApi.getDepartment(authHeader = "Bearer $accessToken")
                if (result.body() != null) {
                    //add the success response to the livedata
                    departmentResponse.value = Response.Success(data = result.body())
                    departmentResponse.value = Response.Loading(showLoader = false)
                } else {
                    //add the error response to the livedata
                    try {
                        departmentResponse.value =
                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    } catch (e: JSONException) {
                        departmentResponse.value = Response.Error(errorMessage = result.message())
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                //add the error response to the livedata
                departmentResponse.value = Response.Error(errorMessage = e.message.toString())
                departmentResponse.value = Response.Loading(showLoader = false)
            }
        } else {
            //add the error response to the livedata
            departmentResponse.value = Response.Error(errorMessage = "No Internet Connection")
        }
    }

    suspend fun createEmployee(requestBody: RequestBody) {

        // Check for internet connection availability
        if (NetworkUtils.isInternetAvailable(context = context)) {
            //activate the loader (progress dialog)
            createEmployeeResponse.value = Response.Loading(showLoader = true)
            try {
                //get access token
                val accessToken = sessionManager.fetchAuthToken()
                //trigger the login api call and get the result

                val result = serverApi.createEmployee(requestBody = requestBody,
                    authHeader = "Bearer $accessToken"
                )

                if (result.body() != null) {
                    //add the success response to the livedata
                    createEmployeeResponse.value = Response.Success(data = result.body())
                    createEmployeeResponse.value = Response.Loading(showLoader = false)
                } else {
                    //add the error response to the livedata
                    try {
                        createEmployeeResponse.value =
                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    } catch (e: JSONException) {
                        createEmployeeResponse.value =
                            Response.Error(errorMessage = result.message())
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                //add the error response to the livedata
                createEmployeeResponse.value = Response.Error(errorMessage = e.message.toString())
                createEmployeeResponse.value = Response.Loading(showLoader = false)
            }
        } else {
            //add the error response to the livedata
            createEmployeeResponse.value = Response.Error(errorMessage = "No Internet Connection")
        }
    }

    /**
     * This function returns the message from the errorbody of the responseBody.
     */
    private fun getErrorBodyMessage(responseBody: ResponseBody): String {
        val errorJson = JSONObject(responseBody.string())
        return errorJson.getString("message")
    }

}