package com.app.tourguide.ui.avaliableplaces

import android.arch.lifecycle.MutableLiveData
import com.app.tourguide.api.model.MyViewModel
import com.app.tourguide.ui.avaliableplaces.model.ResponseAvailableTour
import com.app.tourguide.ui.placedetail.pojomodel.ResponsePlaceDetail

class AvaliablePlacesModel : MyViewModel() {

    var responsePlaces = MutableLiveData<ResponseAvailableTour>()
    var postDeviceTokenResp = MutableLiveData<ResponsePlaceDetail>()

    fun getPackagesData(deviceId: String) {

        isLoading.value = true
        AvaliablePlacesRespository.getAvaliablePlaces({
            responsePlaces.value = it
            isLoading.value = false
        }, {
            apiError.value = it
            isLoading.value = false
        }, {
            onFailure.value = it
            isLoading.value = false
        }, deviceId)
    }


    fun postDeviceToken(deviceId: String, macAddress: String) {

        isLoading.value = true
        AvaliablePlacesRespository.postDeviceToken({ it ->
            postDeviceTokenResp.value = it
            isLoading.value = false
        }, {
            apiError.value = it
            isLoading.value = false
        }, {
            onFailure.value = it
            isLoading.value = false
        }, deviceId,macAddress)
    }

}
