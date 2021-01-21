package com.app.tourguide.ui.mapBox

import com.app.tourguide.api.service.ApiHelper
import com.app.tourguide.api.service.ApiHelperMain
import com.app.tourguide.ui.mapBox.response.PackageSpotsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MapBoxRepository {

    private val webService = ApiHelperMain.createService()

    fun getPackageSpots(successHandler: (PackageSpotsResponse) -> Unit,
                        failureHandler: (String) -> Unit,
                        onFailure: (Throwable) -> Unit, pckgId: String, deviceId: String) {
        webService.getPackageSpots(pckgId, deviceId).enqueue(object : Callback<PackageSpotsResponse> {
            override fun onResponse(call: Call<PackageSpotsResponse>?, response: Response<PackageSpotsResponse>?) {
                response?.body()?.let {
                    successHandler(it)
                }

                if (response?.code() == 422) {
                    response.errorBody()?.let {
                        val error = ApiHelper.handleAuthenticationError(response.errorBody()!!)
                        failureHandler(error)
                    }

                } else {
                    response?.errorBody()?.let {
                        val error = ApiHelper.handleApiError(response.errorBody()!!)
                        failureHandler(error)
                    }
                }
            }

            override fun onFailure(call: Call<PackageSpotsResponse>?, t: Throwable?) {
                t?.let {
                    onFailure(it)
                }
            }
        })
    }

}