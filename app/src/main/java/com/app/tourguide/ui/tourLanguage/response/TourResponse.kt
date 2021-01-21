package com.app.tourguide.ui.tourLanguage.response

import com.google.gson.annotations.SerializedName

data class TourResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)