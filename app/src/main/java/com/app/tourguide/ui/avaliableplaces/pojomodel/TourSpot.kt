package com.app.tourguide.ui.avaliableplaces.pojomodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TourSpot(

        @field:SerializedName("id")
        val tourSpotId: String? = null,

        @field:SerializedName("p_id")
        val pckgId: String? = null,

        @field:SerializedName("name")
        val tourSpotName: String? = null,

        @field:SerializedName("v_thumbnail")
        val tourSpotThumbNail: String? = null,

        @field:SerializedName("video_url")
        val tourSpotVideoUrl: String? = null,

        @field:SerializedName("spot_language")
        val tourSpotLanguage: String? = null,

        @field:SerializedName("location")
        val tourSpotLocation: String? = null,

        @field:SerializedName("status")
        val tourSpotStatus: String? = null,

        @field:SerializedName("created")
        val tourSpotCreated: String? = null

) : Serializable