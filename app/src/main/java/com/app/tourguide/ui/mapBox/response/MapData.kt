package com.app.tourguide.ui.mapBox.response

import com.google.gson.annotations.SerializedName

data class MapData(

	@field:SerializedName("booth_long")
	val boothLong: String? = null,

	@field:SerializedName("tour_time")
	val tourTime: String? = null,

	@field:SerializedName("min_latitude")
	val minLatitude: String? = null,

	@field:SerializedName("max_longitude")
	val maxLongitude: String? = null,

	@field:SerializedName("package_id")
	val packageId: String? = null,

	@field:SerializedName("max_latitude")
	val maxLatitude: String? = null,

	@field:SerializedName("booth_name")
	val boothName: String? = null,

	@field:SerializedName("min_distance")
	val minDistance: String? = null,

	@field:SerializedName("min_zoom")
	val minZoom: String? = null,

	@field:SerializedName("booth_lat")
	val boothLat: String? = null,

	@field:SerializedName("tour_name_language")
	val tourNameLanguage: String? = null,

	@field:SerializedName("region_name")
	val regionName: String? = null,

	@field:SerializedName("max_zoom")
	val maxZoom: String? = null,

	@field:SerializedName("min_longitude")
	val minLongitude: String? = null,

	@field:SerializedName("max_distance")
	val maxDistance: String? = null
)