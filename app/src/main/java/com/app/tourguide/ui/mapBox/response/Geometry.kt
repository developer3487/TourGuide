package com.app.tourguide.ui.mapBox.response

import com.google.gson.annotations.SerializedName

data class Geometry(

        @field:SerializedName("coordinates")
        val coordinates: List<List<Double?>?>? = null,

        @field:SerializedName("type")
        val type: String? = null
)