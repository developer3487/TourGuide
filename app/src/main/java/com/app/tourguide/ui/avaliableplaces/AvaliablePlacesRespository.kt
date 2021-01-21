package com.app.tourguide.ui.avaliableplaces

import com.app.tourguide.api.service.ApiHelper
import com.app.tourguide.api.service.ApiHelperMain
import com.app.tourguide.ui.avaliableplaces.model.ResponseAvailableTour
import com.app.tourguide.ui.placedetail.pojomodel.ResponsePlaceDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AvaliablePlacesRespository {
    private val webService = ApiHelperMain.createService()

    fun getAvaliablePlaces(successHandler: (ResponseAvailableTour) -> Unit,
                           failureHandler: (String) -> Unit,
                           onFailure: (Throwable) -> Unit, deviceId: String) {
        webService.getAvaliableTours(deviceId).enqueue(object : Callback<ResponseAvailableTour> {
            override fun onResponse(call: Call<ResponseAvailableTour>?, response: Response<ResponseAvailableTour>?) {
                response?.body()?.let {

                    /* if (it.success.equals("N")) {
                         failureHandler(it?.message!!)
                         return
                     }*/
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

            override fun onFailure(call: Call<ResponseAvailableTour>?, t: Throwable?) {
                t?.let {
                    onFailure(it)
                }
            }
        })
    }


    fun postDeviceToken(successHandler: (ResponsePlaceDetail) -> Unit,
                        failureHandler: (String) -> Unit,
                        onFailure: (Throwable) -> Unit, deviceId: String, macAddress: String) {
        webService.postDeviceId(deviceId,macAddress).enqueue(object : Callback<ResponsePlaceDetail> {
            override fun onResponse(call: Call<ResponsePlaceDetail>?, response: Response<ResponsePlaceDetail>?) {
                response?.body()?.let {

                    /* if (it.success.equals("N")) {
                         failureHandler(it?.message!!)
                         return
                     }*/
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

            override fun onFailure(call: Call<ResponsePlaceDetail>?, t: Throwable?) {
                t?.let {
                    onFailure(it)
                }
            }
        })
    }

}