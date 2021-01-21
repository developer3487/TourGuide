package com.app.tourguide.ui.placedetail.pojomodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SpotsItem(

	@field:SerializedName("video_url")
	val videoUrl: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("spot_language")
	val spotLanguage: String? = null,

	@field:SerializedName("v_thumbnail")
	val vThumbnail: String? = null,

	@field:SerializedName("spot_name")
	val spotName: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
):Serializable