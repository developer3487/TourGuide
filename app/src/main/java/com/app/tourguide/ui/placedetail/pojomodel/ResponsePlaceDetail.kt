package com.app.tourguide.ui.placedetail.pojomodel


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ResponsePlaceDetail(

        @field:SerializedName("data")
        val data: Data? = null,


        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
) : Serializable