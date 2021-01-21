package com.app.tourguide.ui.avaliableplaces.pojomodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataItem(

        @field:SerializedName("TourPackage")
        val tourPackage: TourPackageItem? = null,

        @field:SerializedName("locations")
        val location: ArrayList<Location>? = null,

        @field:SerializedName("TourSpot")
        val tourSpot: ArrayList<TourSpot>? = null
) : Serializable