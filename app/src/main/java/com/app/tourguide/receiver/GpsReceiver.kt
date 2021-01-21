package com.app.tourguide.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.app.tourguide.listeners.LocationCallBack

/**
 * initializes receiver with callback
 * @param iLocationCallBack Location callback
 */
class GpsReceiver(private val locationCallBack: LocationCallBack) : BroadcastReceiver() {

    /**
     * triggers on receiving external broadcast
     * @param context Context
     * @param intent Intent
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action!!.matches("android.location.PROVIDERS_CHANGED".toRegex())) {
            locationCallBack.onLocationTriggered()
        }
    }
}
