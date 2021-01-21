package com.app.tourguide.ui.avaliableplaces.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.collections.ArrayList

data class ResponseAvailableTour(

        @field:SerializedName("data")
        val data: ArrayList<DataItem?>? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
) : Serializable