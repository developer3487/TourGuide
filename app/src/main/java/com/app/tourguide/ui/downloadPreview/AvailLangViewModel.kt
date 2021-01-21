package com.app.tourguide.ui.downloadPreview

import android.arch.lifecycle.MutableLiveData
import com.app.tourguide.api.model.MyViewModel
import com.app.tourguide.ui.downloadPreview.pojomodel.ResponseAvailLang

class AvailLangViewModel : MyViewModel() {

    var response = MutableLiveData<ResponseAvailLang>()

    fun getAvailLang(packageId: String, deviceId: String) {
        isLoading.value = true
        AvailLangRespository.getAvailData({
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

    var respActivLang = MutableLiveData<ResponseAvailLang>()

    fun updateActivLang(type: String, deviceId: String) {
        isLoading.value = true
        AvailLangRespository.updateActivLang({
            respActivLang.value = it
            isLoading.value = false
        }, {
            apiError.value = it
            isLoading.value = false
        }, {
            onFailure.value = it
            isLoading.value = false
        }, type, deviceId)
    }

}
