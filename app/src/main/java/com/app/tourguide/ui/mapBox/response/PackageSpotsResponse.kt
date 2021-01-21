package com.app.tourguide.ui.mapBox.response

import com.google.gson.annotations.SerializedName

data class PackageSpotsResponse(

	@field:SerializedName("mapData")
	val mapData: MapData? = null,

	@field:SerializedName("route")
	val route: Route? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)