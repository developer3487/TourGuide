package com.app.tourguide.data.login

import com.google.gson.annotations.SerializedName

data class Errors(

		@field:SerializedName("password")
    	val password: List<String?>? = null,

	 	@field:SerializedName("role")
	    val role: List<String?>? = null,

		@field:SerializedName("device_token")
	    val deviceToken: List<String?>? = null,

		@field:SerializedName("email")
		val email: List<String?>? = null,

		@field:SerializedName("phone_number")
		val mobile: List<String?>? = null

)