package com.app.tourguide.ui.avaliableplaces.pojomodel


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TourPackageItem(

        @field:SerializedName("p_name")
        val tourName: String? = null,

        @field:SerializedName("package_id")
        val tourPckgId: String? = null

) : Serializable