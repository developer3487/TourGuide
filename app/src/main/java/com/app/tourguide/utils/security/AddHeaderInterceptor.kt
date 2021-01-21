package com.hmu.kotlin.utils.security

import Preferences
import android.util.Log
import com.app.tourguide.utils.Constants
import com.app.tourguide.utils.getValue
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*


/**
 * Created by shivam on 9/4/18.
 */


class AddHeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val builder = chain.request().newBuilder()
        val token = "Bearer " + Preferences.prefs?.getValue(Constants.TOKEN, "")
        builder.addHeader("timezone",getTimeZone())
        builder.addHeader("Authorization", token)

        getTimeZone()
        return chain.proceed(builder.build())

    }

    fun getTimeZone():String{
        val tz = TimeZone.getDefault()
        val offset = tz.rawOffset
        val timeZone = String.format("%s%02d:%02d", if (offset >= 0) "+" else "-", offset / 3600000, offset / 60000 % 60)
        Log.e("newTimezone",timeZone)
        return timeZone
    }
   
}