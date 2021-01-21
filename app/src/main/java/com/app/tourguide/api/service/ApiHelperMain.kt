package com.app.tourguide.api.service

import com.app.tourguide.application.Application
import com.app.tourguide.data.Status
import com.app.tourguide.data.login.ErrorResponse
import com.app.tourguide.utils.Constants
import com.google.gson.GsonBuilder
import com.hmu.kotlin.utils.security.AddHeaderInterceptor
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiHelperMain {

    private var mRetrofit: Retrofit

    // Creating Retrofit Object
    init {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        val gson = gsonBuilder.create()

        mRetrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getClient())
                .build()
    }

    // Creating OkHttpclient Object
    private fun getClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .addInterceptor(ChuckInterceptor(Application.getContext()))
                //.addInterceptor(ResponseInterceptorterceptor())
                .addNetworkInterceptor(AddHeaderInterceptor())
                .build()

    }

    //Creating service class for calling the web services
    fun createService(): WebService {
        return mRetrofit.create(WebService::class.java)
    }

    // Handling error messages returned by Apis
    fun handleApiError(body: ResponseBody?): String {
        var errorMsg = Constants.SOMETHING_WENT_WRONG
        try {
            val errorConverter: Converter<ResponseBody, Status> = mRetrofit.responseBodyConverter(Status::class.java,
                    arrayOfNulls(0))
            val error: Status = errorConverter.convert(body)
            errorMsg = error.message
        } catch (e: Exception) {
        }

        return errorMsg
    }

    fun handleAuthenticationError(body: ResponseBody?): String {
        val errorConverter: Converter<ResponseBody, ErrorResponse> = mRetrofit.responseBodyConverter(ErrorResponse::class.java, arrayOfNulls(0))
        val errorResponse: ErrorResponse = errorConverter.convert(body)
        var errorMsg = errorResponse.message!!
        val email = errorResponse.errors?.email
        val password = errorResponse.errors?.password
        val role = errorResponse.errors?.role
        val deviceToken = errorResponse.errors?.deviceToken
        val mobileNumber = errorResponse.errors?.mobile


        if (email != null && email.isNotEmpty()) {
            errorMsg = email[0].toString()
        } else if (password != null && password.isNotEmpty()) {
            errorMsg = password[0].toString()
        } else if (role != null && role.isNotEmpty()) {
            errorMsg = role[0].toString()

        } else if (deviceToken != null && deviceToken.isNotEmpty()) {
            errorMsg = (deviceToken[0].toString())
        } else if (mobileNumber != null && mobileNumber.isNotEmpty()) {
            errorMsg = mobileNumber[0].toString()
        }
        return errorMsg
    }


}