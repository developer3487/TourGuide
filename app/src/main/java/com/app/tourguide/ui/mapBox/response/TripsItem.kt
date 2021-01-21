package com.app.tourguide.ui.mapBox.response

import com.google.gson.annotations.SerializedName

data class TripsItem(

	@field:SerializedName("duration")
	val duration: Double? = null,

	@field:SerializedName("distance")
	val distance: Double? = null,

	@field:SerializedName("legs")
	val legs: List<LegsItem?>? = null,

	@field:SerializedName("weight_name")
	val weightName: String? = null,

	@field:SerializedName("weight")
	val weight: Double? = null,

	@field:SerializedName("geometry")
	val geometry: Geometry? = null
)