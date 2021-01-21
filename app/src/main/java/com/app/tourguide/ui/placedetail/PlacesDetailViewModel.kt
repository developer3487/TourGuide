package com.app.tourguide.ui.placedetail

import android.arch.lifecycle.MutableLiveData
import com.app.tourguide.api.model.MyViewModel
import com.app.tourguide.ui.downloadPreview.AvailLangRespository
import com.app.tourguide.ui.downloadPreview.pojomodel.ResponseAvailLang
import com.app.tourguide.ui.placedetail.pojomodel.ResponsePlaceDetail

class PlacesDetailViewModel : MyViewModel() {

    var response = MutableLiveData<ResponsePlaceDetail>()

    var tourPreviewResponse = MutableLiveData<ResponseAvailLang>()


    fun getPlacesDetails(packageId: String, deviceId: String) {

        isLoading.value = true
        PlaceDetailRespository.getPlaceDetail({
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

    var otpResponse = MutableLiveData<ResponsePlaceDetail>()

    fun sendOtp(otpCode: String, deviceId: String, packageId: String) {

        isLoading.value = true
        PlaceDetailRespository.sendOtp({
            otpResponse.value = it
            isLoading.value = false
        }, {
            apiError.value = it
            isLoading.value = false
        }, {
            onFailure.value = it
            isLoading.value = false
        }, otpCode, deviceId, packageId)
    }


    fun getAvailLang(packageId: String, deviceId: String) {
        // isLoading.value = true
        PlaceDetailRespository.getAvailData({
            tourPreviewResponse.value = it
            //isLoading.value = false
        }, {
            apiError.value = it
            //isLoading.value = false
        }, {
            onFailure.value = it
            //isLoading.value = false
        }, packageId, deviceId)
    }

}
