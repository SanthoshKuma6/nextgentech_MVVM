package com.nextgenapp.nextgentech.data.api

import com.google.gson.JsonObject
import com.nextgenapp.nextgentech.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    /**
     * ApiInterface to make the api request to get the desired responses
     * @POST - share the parameters in @Body to get the response
     * @GET - get the response
     *
     * Response - add pojo classes to the Response (retrofit object)
     */

    @POST("login")
    suspend fun login(@Body jsonObject: JsonObject): Response<LoginResponse>

    @GET("employee")
    suspend fun getEmployeeList(@Header("Authorization") authHeader: String): Response<EmployeeListResponse>

    @GET("employee/{employeeId}")
    suspend fun getEmployee(
        @Path("employeeId") employeeId: String,
        @Header("Authorization") authHeader: String
    ): Response<EmployeeResponse>

    @GET("department")
    suspend fun getDepartment(@Header("Authorization") authHeader: String): Response<DepartmentResponse>

    @POST("employee")
    suspend fun createEmployee(
        @Body requestBody: RequestBody,
        @Header("Authorization") authHeader: String
    ): Response<EmployeeCreateResponse>

}