package com.nextgenapp.nextgentech.data.model

import com.google.gson.annotations.SerializedName

/**
 * LoginResponse - Pojo class for the login api response.
 */

data class LoginResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
){
	data class Data(

		@field:SerializedName("name")
		val name: String? = null,

		@field:SerializedName("message")
		val message: String? = null,

		@field:SerializedName("token")
		val token: String? = null
	)
}
