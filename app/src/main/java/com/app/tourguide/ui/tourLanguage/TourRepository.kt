package com.app.tourguide.ui.tourLanguage

import com.app.tourguide.api.service.ApiHelper
import com.app.tourguide.api.service.ApiHelperMain
import com.app.tourguide.ui.tourLanguage.response.TourLangResponse
import com.app.tourguide.ui.tourLanguage.response.TourResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TourRepository {

    private val webService = ApiHelperMain.createService()

    fun getTourLanguages(successHandler: (TourResponse) -> Unit,
                     failureHandler: (String) -> Unit,
                     onFailure: (Throwable) -> Unit, pckgId: String, deviceId: String) {
        webService.getTourLanguages(pckgId, deviceId).enqueue(object : Callback<TourResponse> {
            override fun onResponse(call: Call<TourResponse>?, response: Response<TourResponse>?) {
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

            override fun onFailure(call: Call<TourResponse>?, t: Throwable?) {
                t?.let {
                    onFailure(it)
                }
            }
        })
    }


    fun updateTourLanguages(successHandler: (TourLangResponse) -> Unit,
                         failureHandler: (String) -> Unit,
                         onFailure: (Throwable) -> Unit, pckgId: String, deviceId: String,tourPackageId:String) {
        webService.updateTourLanguages(pckgId, deviceId,tourPackageId).enqueue(object : Callback<TourLangResponse> {
            override fun onResponse(call: Call<TourLangResponse>?, response: Response<TourLangResponse>?) {
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

            override fun onFailure(call: Call<TourLangResponse>?, t: Throwable?) {
                t?.let {
                    onFailure(it)
                }
            }
        })
    }

}