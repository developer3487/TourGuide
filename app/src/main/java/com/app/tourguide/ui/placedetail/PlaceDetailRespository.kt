package com.app.tourguide.ui.placedetail

import com.app.tourguide.api.service.ApiHelper
import com.app.tourguide.api.service.ApiHelperMain
import com.app.tourguide.ui.downloadPreview.pojomodel.ResponseAvailLang
import com.app.tourguide.ui.placedetail.pojomodel.ResponsePlaceDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object PlaceDetailRespository {
    private val webService = ApiHelperMain.createService()

    fun getPlaceDetail(successHandler: (ResponsePlaceDetail) -> Unit,
                       failureHandler: (String) -> Unit,
                       onFailure: (Throwable) -> Unit, pckgId: String, deviceId: String) {
        webService.getPlaceDetail(pckgId, deviceId).enqueue(object : Callback<ResponsePlaceDetail> {
            override fun onResponse(call: Call<ResponsePlaceDetail>?, response: Response<ResponsePlaceDetail>?) {
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

            override fun onFailure(call: Call<ResponsePlaceDetail>?, t: Throwable?) {
                t?.let {
                    onFailure(it)
                }
            }
        })
    }

    fun sendOtp(successHandler: (ResponsePlaceDetail) -> Unit,
                       failureHandler: (String) -> Unit,
                       onFailure: (Throwable) -> Unit, otpCode: String, deviceId: String,packageId: String) {
        webService.sendOtp(otpCode, deviceId,packageId).enqueue(object : Callback<ResponsePlaceDetail> {
            override fun onResponse(call: Call<ResponsePlaceDetail>?, response: Response<ResponsePlaceDetail>?) {
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

            override fun onFailure(call: Call<ResponsePlaceDetail>?, t: Throwable?) {
                t?.let {
                    onFailure(it)
                }
            }
        })
    }

    fun getAvailData(successHandler: (ResponseAvailLang) -> Unit,
                     failureHandler: (String) -> Unit,
                     onFailure: (Throwable) -> Unit, pckgId: String, deviceId: String) {
        webService.getPreviewLang(pckgId, deviceId).enqueue(object : Callback<ResponseAvailLang> {
            override fun onResponse(call: Call<ResponseAvailLang>?, response: Response<ResponseAvailLang>?) {
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

            override fun onFailure(call: Call<ResponseAvailLang>?, t: Throwable?) {
                t?.let {
                    onFailure(it)
                }
            }
        })
    }

}