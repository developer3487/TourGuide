package com.app.tourguide.ui.downloadPreview.pojomodel


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ResponseAvailLang(

        @field:SerializedName("data")
        val data: ArrayList<DataAvailLang>? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
) : Serializable