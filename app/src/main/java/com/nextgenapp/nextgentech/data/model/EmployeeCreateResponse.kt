package com.nextgenapp.nextgentech.data.model

import com.google.gson.annotations.SerializedName

data class EmployeeCreateResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
){
	data class Data(
		@field:SerializedName("employee")
		val employee: Employee? = null
	){
		data class Employee(

			@field:SerializedName("address")
			val address: String? = null,

			@field:SerializedName("date_of_birth")
			val dateOfBirth: String? = null,

			@field:SerializedName("department_name")
			val departmentName: String? = null,

			@field:SerializedName("name")
			val name: String? = null,

			@field:SerializedName("photo")
			val photo: String? = null,

			@field:SerializedName("blood_group")
			val bloodGroup: String? = null,

			@field:SerializedName("created_at")
			val createdAt: String? = null,

			@field:SerializedName("mobile_number")
			val mobileNumber: String? = null,

			@field:SerializedName("email")
			val email: String? = null,

			@field:SerializedName("department_token")
			val departmentToken: String? = null,

			@field:SerializedName("token")
			val token: String? = null
		)
	}
}