package com.app.tourguide.ui.mapBox.response

import com.google.gson.annotations.SerializedName

data class DataItem(

	@field:SerializedName("spot_video_vtt")
	val spotVideoVtt: String? = null,

	@field:SerializedName("spot_id")
	val spotId: String? = null,

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("s_language")
	val sLanguage: String? = null,

	@field:SerializedName("file_type")
	val fileType: String? = null,

	@field:SerializedName("s_video")
	val sVideo: String? = null,

	@field:SerializedName("v_thumbnail")
	val vThumbnail: String? = null,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("video_language")
	val videoLanguage: String? = null,

	@field:SerializedName("spot_name")
	val spotName: String? = null,

	@field:SerializedName("updated")
	val updated: String? = null,

	@field:SerializedName("spot_desc")
	val spotDesc: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
)