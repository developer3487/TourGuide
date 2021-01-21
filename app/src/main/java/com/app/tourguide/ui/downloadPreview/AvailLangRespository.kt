package com.app.tourguide.ui.downloadPreview

import com.app.tourguide.api.service.ApiHelper
import com.app.tourguide.api.service.ApiHelperMain
import com.app.tourguide.ui.downloadPreview.pojomodel.ResponseAvailLang
import com.app.tourguide.ui.placedetail.pojomodel.ResponsePlaceDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AvailLangRespository {
    private val webService = ApiHelperMain.createService()

    fun getAvailData(successHandler: (ResponseAvailLang) -> Unit,
                       failureHandler: (String) -> Unit,
                       onFailure: (Throwable) -> Unit, pckgId: String, deviceId: String) {
        webService.getAvailLang(pckgId, deviceId).enqueue(object : Callback<ResponseAvailLang> {
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

    fun updateActivLang(successHandler: (ResponseAvailLang) -> Unit,
                     failureHandler: (String) -> Unit,
                     onFailure: (Throwable) -> Unit, typeActiv: String, deviceId: String) {
        webService.postActiveLang(typeActiv, deviceId).enqueue(object : Callback<ResponseAvailLang> {
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