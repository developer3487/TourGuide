package com.app.tourguide.ui.tourLanguage.response

import com.google.gson.annotations.SerializedName

data class TourLangResponse(

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
)