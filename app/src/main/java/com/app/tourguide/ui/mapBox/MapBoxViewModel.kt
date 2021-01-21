package com.app.tourguide.ui.mapBox

import android.arch.lifecycle.MutableLiveData
import com.app.tourguide.api.model.MyViewModel
import com.app.tourguide.ui.mapBox.response.PackageSpotsResponse

class MapBoxViewModel : MyViewModel() {

    var response = MutableLiveData<PackageSpotsResponse>()

    fun getPackagesData(pckgId: String, deviceId: String) {

        isLoading.value = true
        MapBoxRepository.getPackageSpots({
            response.value = it
            isLoading.value = false
        }, {
            apiError.value = it
            isLoading.value = false
        }, {
            onFailure.value = it
            isLoading.value = false
        }, pckgId, deviceId)
    }

}