package com.app.tourguide.ui.tourLanguage

import android.arch.lifecycle.MutableLiveData
import com.app.tourguide.api.model.MyViewModel
import com.app.tourguide.ui.tourLanguage.response.TourLangResponse
import com.app.tourguide.ui.tourLanguage.response.TourResponse

class TourViewModel :MyViewModel() {

    var response = MutableLiveData<TourResponse>()
    var resposneTourLang= MutableLiveData<TourLangResponse>()

    fun getTourLanguages(packageId: String, deviceId: String) {
        isLoading.value = true
        TourRepository.getTourLanguages({
            response.value = it
            isLoading.value = false
        }, {
            apiError.value = it
            isLoading.value = false
        }, {
            onFailure.value = it
            isLoading.value = false
        }, packageId, deviceId)
    }


    fun updateTourLanguage(packageId: String, deviceId: String,tourPackageId:String) {
        isLoading.value = true
        TourRepository.updateTourLanguages({
            resposneTourLang.value = it
            isLoading.value = false
        }, {
            apiError.value = it
            isLoading.value = false
        }, {
            onFailure.value = it
            isLoading.value = false
        }, packageId, deviceId,tourPackageId)
    }
}