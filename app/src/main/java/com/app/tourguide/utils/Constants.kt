package com.app.tourguide.utils

import com.app.tourguide.BuildConfig

/**
 * Created by android on 2/11/17.
 */
object Constants {


    const val IS_DEVICE_ID_SEND = "is_device_id_send"
    const val BASE_URL = BuildConfig.BASE_URL
    const val TOKEN = "token"
    const val VIDEO_URL = "video_url"

    const val PACKAGE_DATA_ITEM = "package_data_item"
    const val PACKAGE_ID = "package_id"
    const val LOCATIONS = "locations"
    const val SPOTS = "spots"

    const val VIDEO_POSITION = "video_pos"

    const val PERMISSION_READ_EXTERNAL_STORAGE = 123
    const val PERMISSION_REQUEST_CODE = 98

    const val HANDLER_DELAY_TIME: Long = 2000

    const val NOTI_COUNT = "noti_count"


    const val APP_HIDDEN_FOLDER = "/.besttyme"


    const val SNACK_BAR_DURATION = 2500


    const val IS_LOGOUT = "is_logout"

    //FAILURE MESSAGES
    const val SOMETHING_WENT_WRONG = "Something went wrong please try again later!"
    const val FAILURE_TIME_OUT_ERROR = "Request time out!"
    const val FAILURE_SOMETHING_WENT_WRONG = "Something went wrong please try again later!"
    const val FAILURE_SERVER_NOT_RESPONDING = "Oops! looks like we are having internal problems. Please try again later."
    const val FAILURE_INTERNET_CONNECTION = "Internet connection appears to be offline. Please check your network settings."
    const val SESSION_EXPIRED = "Sorry, looks like you are logged in another device with the same user."

    const val PREVIEW_STATUS = "preview_status"
    const val URL_AVL_LOC = "availableLocation"
    const val TOUR_PACKAGE_DETAILS = "tourPackageDetails"
    const val AVAIL_LANGUAGE = "PackageLanguage"
    const val PREVIEW_LANGUAGE = "previewLanguage"
    const val TOUR_LANGUAGE = "activeTourLanguage"
    const val URL_AVL_TOURS = "availableTours"
    const val URL_SAVE_DEVICE_ID = "saveUserToken"
    const val URL_ACTIVE_LANG = "activeLanguage"
    const val URL_DOWNLOAD_PCKG = "DownloadPackage"
    const val URL_PCKG_SPOTS = "packagesSpots"

    const val MAP_STATUS = "MAP_STATUS"

    const val VIEW_VIDEO= "VIEW_VIDEO"

}