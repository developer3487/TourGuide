package com.app.tourguide.api.service

import com.app.tourguide.ui.avaliableplaces.model.ResponseAvailableTour
import com.app.tourguide.ui.avaliableplaces.pojomodel.ResponseAvaliablePlaces
import com.app.tourguide.ui.downloadPreview.pojomodel.ResponseAvailLang
import com.app.tourguide.ui.mapBox.response.PackageSpotsResponse
import com.app.tourguide.ui.placedetail.pojomodel.ResponsePlaceDetail
import com.app.tourguide.ui.tourLanguage.response.TourLangResponse
import com.app.tourguide.ui.tourLanguage.response.TourResponse
import com.app.tourguide.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


/**
 *  All web services are declared here
 */
interface WebService {

    @GET("directions/json?")
    fun getPathDetails(@Query(value = "origin=", encoded = true)
                       source_location: String, @Query(value = "&destination=", encoded = true)
                       final_destination: String, @Query(value = "&sensor=true&key=", encoded = true) server_key: String): Call<ResponseBody>


    @FormUrlEncoded
    @POST(Constants.TOUR_PACKAGE_DETAILS)
    fun getPlaceDetail(@Field("package_id") pckgId: String, @Field("device_id") deviceId: String): Call<ResponsePlaceDetail>

    @FormUrlEncoded
    @POST(Constants.URL_DOWNLOAD_PCKG)
    fun sendOtp(@Field("code") code: String, @Field("device_id") deviceId: String, @Field("package_id") packageId: String): Call<ResponsePlaceDetail>

    @FormUrlEncoded
    @POST(Constants.AVAIL_LANGUAGE)
    fun getAvailLang(@Field("package_id") pckgId: String, @Field("device_id") deviceId: String): Call<ResponseAvailLang>


    @FormUrlEncoded
    @POST(Constants.PREVIEW_LANGUAGE)
    fun getPreviewLang(@Field("package_id") pckgId: String, @Field("device_id") deviceId: String): Call<ResponseAvailLang>


    @FormUrlEncoded
    @POST(Constants.URL_AVL_LOC)
    fun getAvaliablePlaces(@Field("device_id") deviceId: String): Call<ResponseAvaliablePlaces>

    @FormUrlEncoded
    @POST(Constants.URL_AVL_TOURS)
    fun getAvaliableTours(@Field("device_id") deviceId: String): Call<ResponseAvailableTour>

    @FormUrlEncoded
    @POST(Constants.URL_SAVE_DEVICE_ID)
    fun postDeviceId(@Field("device_id") deviceId: String,@Field("device_srno") macAddress: String): Call<ResponsePlaceDetail>

    @FormUrlEncoded
    @POST(Constants.URL_ACTIVE_LANG)
    fun postActiveLang(@Field("active_language") actLang: String, @Field("device_id") deviceId: String): Call<ResponseAvailLang>


    @FormUrlEncoded
    @POST(Constants.AVAIL_LANGUAGE)
    fun getTourLanguages(@Field("package_id") pckgId: String, @Field("device_id") deviceId: String): Call<TourResponse>


    @FormUrlEncoded
    @POST(Constants.URL_PCKG_SPOTS)
    fun getPackageSpots(@Field("package_id") pckgId: String, @Field("device_id") deviceId: String): Call<PackageSpotsResponse>


    @FormUrlEncoded
    @POST(Constants.TOUR_LANGUAGE)
    fun updateTourLanguages(@Field("package_id") pckgId: String, @Field("device_id") deviceId: String, @Field("active_package_language") packageId: String): Call<TourLangResponse>


//    @Headers("Accept: " + "application/json")
//    @POST("login")
//    @FormUrlEncoded
//    fun loginUser(@Field("code") code: String): Call<com.hmu.kotlin.data.pinLogin.LoginResponse>
//
//
//    @POST(Constants.UPDATE_INFO)
//    fun updateInformation(@Body request: ResetPasswordRequest): Call<UpdateInfoPojo>


//
//    @Headers("Accept: " + "application/json")
//    @POST("forgot_password")
//    fun resetPassword(@Body request: ResetPasswordRequest): Call<Status>
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("signup")
//    fun register(@Body request: RegisterationRequest): Call<Status>


//
//    @Headers("Accept: " + "application/json")
//    @POST("mr-register")
//    fun registeration(@Body request: RegisterationRequest): Call<RegisterationResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("change-password")
//    fun changePassword(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("contact-us")
//    fun contactUs(@Body request: ContactUsRequest): Call<ContactUsResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @GET("master-data/products-list?locName=Sa")
//    fun productsPromotedList(@Query("locName") value: String,
//                             @Query("start") start: Int,
//                             @Query("length") length: Int): Call<ProductListResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @GET("master-data/register-fields")
//    fun getIndusteryList(): Call<RegisterFeildResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @GET("view-doctor")
//    fun doctorDetails(@Query("id") doctorId:Int): Call<ViewDoctorDetailsResponse>
//
//    @Headers("Accept: " + "application/json")
//    @GET("contact")
//    fun unverifiedDoctorDetails(@Query("id") doctorId:Int): Call<UnverifiedDoctorResponse>
//    fun doctorDetails(@Header("Authorization") token: String?, @Query("id") doctorId: Int?): Call<ViewDoctorDetailsResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @GET("master-data/specialty-list")
//    fun getSpecializationList(): Call<SpecializationListResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("search-doctor")
//    fun searchDoctor(@Body request: SearchDoctorRequeest): Call<SearchDoctorResponse>
//
//    @Headers("Accept: " + "application/json")
//    @POST("appointment")
//    fun bookSlot(@Body request: BookingRequest): Call<BookingResponse>
//
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("appointment")
//    fun bookSlotUnverifiedDoctor(@Body request: UnverifiedDoctorBookingRequest): Call<BookingResponse>
//
//    @Headers("Accept: " + "application/json")
//    @GET("doctor-schedule")
//    fun getDoctorSchedule(@Query("id") value: Int?,
//                          @Query("address_id") addressId: Int?,
//                          @Query("date") date: String?): Call<DoctorScheduleReponse>
//
//    @Headers("Accept: " + "application/json")
//    @GET("contact-schedule")
//    fun getUnverifiedDoctorSchedule(@Query("id") value: Int?,
//
//                          @Query("date") date: String?): Call<DoctorScheduleReponse>
//
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("last-visits")
//    fun appointmentsList(@Body request: ViewAnalyticsRequest): Call<ViewAnalyticsResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("appointments-list")
//    fun viewScheduleList(@Body request: ViewScheduleRequest): Call<ViewScheduleResponse>
//
//    @Headers("Accept: " + "application/json")
//    @POST("contact")
//    fun createContacts( @Body request: ContactRequest): Call<ContactResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("send-invitation")
//    fun sendInvitation(@Body request: InviteDoctorRequest): Call<InviteDoctorResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @HTTP(method = "DELETE", path = "contact", hasBody = true)
//    fun deleteContact( @Body request: DeleteUnverifiedDoctorRequest ): Call<DeleteUnverifiedDoctorResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @GET("contact-sugesstion")
//    fun getViewSuggestionList(@Query("id") id: Int?
//                              ,@Query("start") start: Int?
//                              ,@Query("length") length: Int?): Call<SuggestionResponse>
//
//
//
//    @Headers("Accept: " + "application/json")
//    @GET("notifications")
//    fun getNotificationList(@Query("page") page: Int?
//                              ): Call<NotificationResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @GET("contact-sugesstion?id=1465")
//    fun getViewSuggestionList(@Header("Authorization") token: String?): Call<ViewSuggestionResponse>
//
//    @Headers("Accept: " + "application/json")
//    @GET("view-contact")
//    fun getUserDetails(@Query("id") id: Int?): Call<GetUserDetailResponse>
//
//    @Headers("Accept: " + "application/json")
//    @POST("todo")
//    fun getTodoList(@Body request: TodoRequest): Call<TodoResponse>
//
//    @Headers("Accept: " + "application/json")
//    @POST("confirm-sugesstion")
//    fun addToContact(@Body request: AddToContactRequest): Call<AddToContactResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("confirm-doctor")
//    fun addToContactWithPriority(@Body request: AddToPriorityContactRequest): Call<AddToContactResponse>
//
//
//
//
//    @Headers("Accept: " + "application/json")
//    @PUT("contact")
//    fun updateToContact(@Body request: ContactRequest): Call<ContactResponse>
//
//    @Headers("Accept: " + "application/json")
//    @PUT("profile")
//    fun updateProfile(@Body request: RegisterationRequest): Call<UpdateProfileSignUpResponse>
//
//    @Headers("Accept: " + "application/json")
//    @HTTP(method = "DELETE", path = "appointment", hasBody = true)
//    fun deleteAppointment( @Body request: DeleteAppointmentRequest ): Call<DeleteAppointmentResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @HTTP(method = "DELETE", path = "todo/{id}")
//    fun deleteTodo(@Path("id")  id: String): Call<DeleteAppointmentResponse>
//
//
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("push-notification")
//    fun pushNotification(@Body request: PushNotificationRequest): Call<PushNotificationResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @DELETE("delete-account")
//    fun deleteAccount(): Call<DeleteAccountResponse>
//
//
//
//    @Headers("Accept: " + "application/json")
//    @GET("sample-requests")
//    fun getSampleList(@Query("page") page: Int? ): Call<SampleRequestResponse>
//
//
//
//    @Headers("Accept: " + "application/json")
//    @PUT("sample-request")
//    fun acceptRejectSampleCall(@Body request: SampleAcceptRejectRequest): Call<SampleAcceptRejectResponse>
//
//
//
//    @Headers("Accept: " + "application/json")
//    @POST("appointment-sugesstions")
//    fun suggestionList(@Body request: SuggestionRequest): Call<ViewScheduleResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @PUT("token")
//    fun updateFirebaseDeviceToken(@Body request: UpdateTokenRequest): Call<UpdateTokenResponse>
//
//    @Headers("Accept: " + "application/json")
//    @HTTP(method = "DELETE", path = "sample-request", hasBody = true)
//    fun deleteSample( @Body request: DeleteSampleRequest ): Call<DeleteSampleResponse>
//
//
//    @Headers("Accept: " + "application/json")
//    @HTTP(method = "DELETE", path = "notification", hasBody = true)
//    fun deleteNotification( @Body request: NotificationRequest): Call<NotificationOperationResponse>
//
//    @Headers("Accept: " + "application/json")
//    @PUT("notification")
//    fun updateNotificationStatus(@Body request: NotificationRequest): Call<NotificationOperationResponse>
//
//    @Headers("Accept: " + "application/json")
//    @GET("notification-count")
//    fun getNotificationCount(): Call<NotificationCountResponse>


}