package com.app.tourguide.ui.placedetail.pojomodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Data(

        @field:SerializedName("p_name")
        val pName: String? = null,

        @field:SerializedName("Spots")
        val spots: List<SpotsItem>? = null,

        @field:SerializedName("p_description")
        val pDescription: String? = null,

        @field:SerializedName("p_amount")
        val pAmount: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("l_id")
        val lId: String? = null,

        @field:SerializedName("available_in")
        val availableIn: String? = null,

        @field:SerializedName("p_image")
        val pImage: String? = null
) : Serializable