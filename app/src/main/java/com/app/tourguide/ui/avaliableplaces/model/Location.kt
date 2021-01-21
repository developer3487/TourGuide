package com.app.tourguide.ui.avaliableplaces.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location(

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("location")
        val location: String? = null,

        @field:SerializedName("latitude")
        val latitude: String? = null,

        @field:SerializedName("longitude")
        val longitude: String? = null
) : Serializable