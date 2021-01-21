package com.app.tourguide.ui.avaliableplaces.model

import android.arch.persistence.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class TourPackagesItem(

	@field:SerializedName("p_name")
	val pName: String? = null,

	@field:SerializedName("package_id")
	val packageId: String? = null,

	@field:SerializedName("id")
	val id: String? = null
): Serializable