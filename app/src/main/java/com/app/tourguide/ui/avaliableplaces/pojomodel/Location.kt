package com.app.tourguide.ui.avaliableplaces.pojomodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location(

        @field:SerializedName("l_id")
        val locId: String? = null,

        @field:SerializedName("l_name")
        val locName: String? = null,

        @field:SerializedName("latitude")
        val locLatt: String? = null,

        @field:SerializedName("longitude")
        val locLong: String? = null,

        @field:SerializedName("v_thumbnail")
        val locVideoThumb: String? = null,

        @field:SerializedName("video_url")
        val locVideo: String? = null


) : Serializable