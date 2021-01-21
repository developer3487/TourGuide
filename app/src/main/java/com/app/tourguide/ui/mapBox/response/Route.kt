package com.app.tourguide.ui.mapBox.response

import com.google.gson.annotations.SerializedName

data class Route(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("trips")
	val trips: List<TripsItem?>? = null,

	@field:SerializedName("waypoints")
	val waypoints: List<WaypointsItem?>? = null
)