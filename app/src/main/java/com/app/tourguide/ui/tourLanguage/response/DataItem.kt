package com.app.tourguide.ui.tourLanguage.response

import com.google.gson.annotations.SerializedName

data class DataItem(

	@field:SerializedName("video_url")
	val videoUrl: String? = null,

	@field:SerializedName("icon")
	val icon: String? = null,

	@field:SerializedName("language")
	val language: String? = null,

	@field:SerializedName("language_id")
	val languageId: String? = null
)