package com.app.tourguide.ui.mapBox.response

import com.google.gson.annotations.SerializedName

data class WaypointsItem(

	@field:SerializedName("distance")
	val distance: Double? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("location")
	val location: List<Double?>? = null,

	@field:SerializedName("waypoint_index")
	val waypointIndex: Int? = null,

	@field:SerializedName("trips_index")
	val tripsIndex: Int? = null
)