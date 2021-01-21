package com.app.tourguide.ui.mapBox.response

import com.google.gson.annotations.SerializedName

data class LegsItem(

	@field:SerializedName("summary")
	val summary: String? = null,

	@field:SerializedName("duration")
	val duration: Double? = null,

	@field:SerializedName("distance")
	val distance: Double? = null,

	@field:SerializedName("weight")
	val weight: Double? = null,

	@field:SerializedName("steps")
	val steps: List<Any?>? = null
)