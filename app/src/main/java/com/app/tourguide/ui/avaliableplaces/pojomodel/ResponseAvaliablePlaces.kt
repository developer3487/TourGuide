package com.app.tourguide.ui.avaliableplaces.pojomodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseAvaliablePlaces(

        @field:SerializedName("data")
        val data: MutableList<DataItem>? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
) : Serializable