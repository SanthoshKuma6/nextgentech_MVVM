package com.nextgenapp.nextgentech.ui.viewmodel

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.nextgenapp.nextgentech.data.repo.Repository
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EmployeeViewModel() : ViewModel() {
    /**
     * ViewModel - The ViewModel class is a business logic or screen level state holder.
     * It exposes state to the UI and encapsulates related business logic.
     */

    //repository contains the api call functions
    private val repository by lazy { Repository() }

    //initialize the live data in view model and will be accessed from activity
    val employeeListResponse by lazy { repository.employeeListResponse }
    val employeeResponse by lazy { repository.employeeResponse }
    val departmentResponse by lazy { repository.departmentResponse }
    val createEmployeeResponse by lazy { repository.createEmployeeResponse }


    //error handler liveData
    val errorHandlerLiveData: MutableLiveData<String> = MutableLiveData()

    //call the repository function to trigger the api call
    suspend fun getEmployeeList() = repository.getEmployeeList()

    //api call to get the employee details , share the employee id
    suspend fun getEmployee(employeeId: String) = repository.getEmployee(employeeId = employeeId)

    //get department list
    suspend fun getDepartment() = repository.getDepartment()

    //api to create employee
    suspend fun createEmployee(
        department_token: String,
        name: String,
        email: String,
        mobile_number: String,
        date_of_birth: String,
        blood_group: String,
        address: String,
        photo: RequestBody?,
        fileName: String
    ) {

        when {
            TextUtils.isEmpty(department_token) -> errorHandlerLiveData.value =
                "Please select department"
            TextUtils.isEmpty(name) -> errorHandlerLiveData.value = "Please enter employee name"
            TextUtils.isEmpty(email) -> errorHandlerLiveData.value =
                "Please enter employee email address"
            TextUtils.isEmpty(mobile_number) -> errorHandlerLiveData.value =
                "Please enter employee contact number"
            TextUtils.isEmpty(date_of_birth) -> errorHandlerLiveData.value =
                "Please enter employee date of birth"
            TextUtils.isEmpty(blood_group) -> errorHandlerLiveData.value =
                "Please enter employee blood group"
            TextUtils.isEmpty(address) -> errorHandlerLiveData.value =
                "Please enter employee address"
            else -> {

                val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                builder.addFormDataPart("department_token" , department_token)
                builder.addFormDataPart("name" , name)
                builder.addFormDataPart("email" , email)
                builder.addFormDataPart("mobile_number" , mobile_number)
                builder.addFormDataPart("date_of_birth" , date_of_birth)
                builder.addFormDataPart("blood_group" , blood_group)
                builder.addFormDataPart("address" , address)
                builder.addFormDataPart("photo" , fileName, photo!!)

                val requestBody = builder.build()

                repository.createEmployee(requestBody = requestBody)
            }
        }

    }
}