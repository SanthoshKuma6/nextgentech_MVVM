package com.nextgenapp.nextgentech.data.model

import com.google.gson.annotations.SerializedName

data class DepartmentResponse(

	@field:SerializedName("data")
	val data: ArrayList<DataItem?>? = null
){
	data class DataItem(

		@field:SerializedName("name")
		val name: String? = null,

		@field:SerializedName("created_at")
		val createdAt: String? = null,

		@field:SerializedName("token")
		val token: String? = null
	)
}
