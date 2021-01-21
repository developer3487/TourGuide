package com.app.tourguide.utils

import Preferences
import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.app.tourguide.activity.MainActivity
import com.app.tourguide.R
import com.app.tourguide.cryptography.SharedPreferencesEncryption
import com.app.tourguide.utils.Constants.PERMISSION_READ_EXTERNAL_STORAGE
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by android on 2/11/17.
 * *
 */
object Utils {
    private var mLastClickTime = 0L
    private val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private var mIsPAsswordShown: Boolean = false

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.findViewById<View>(android.R.id.content)
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }



    }


    /**
     * Show toast.
     *
     * @param toast String value which needs to shown in the toast.
     * @description if you want to print a toast just call this method and pass
     * what you want to be shown.
     */
    @Synchronized
    fun showSnackbar(content: View?, toast: String) {
        val snackbar = Snackbar.make(content!!, toast, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        val tv = snackbarView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        tv.maxLines = 3
        snackbar.duration = Constants.SNACK_BAR_DURATION

        snackbar.show()
    }


    fun logout(context: Context) {
        //clear shared preference
        Preferences.prefs?.EncryptedEditor()?.clear()?.commit()
        //Preferences.prefs?.saveValue(Constants.IS_DEVICE_ID_SEND, false)
        Preferences.prefs?.saveValue(Constants.IS_LOGOUT, 1)

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        if (context is Activity) {
            val activity = context
            activity.finish()
            activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
        }
    }

    /**
     * Is email valid.
     *
     * @param email Pass a email in string format and this method check if it is
     * a valid address or not.
     * @return True if email is valid otherwise false.
     */
    fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }


    fun isPasswordValid(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher

        val PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[$@$!%*?&])[a-zA-Z\\d$@$!%*?&]{6,}"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        return matcher.matches()
    }


    fun datetimeFormat(format: String, dateString: String?): String? {

        var formatedResult = dateString

        /* first change the datetime string to date*/
        val dateFormat = SimpleDateFormat(DATE_TIME_FORMAT)

        try {
            /* in this method we are separate the time and date by format of date and time*/
            val date = dateFormat.parse(dateString)

            val formateDate = SimpleDateFormat(format) //

            formatedResult = formateDate.format(date.time)


        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return formatedResult


    }


    /**
     * This method will handle single click
     *
     * @return
     */
    fun singleClick(): Boolean {
        val minInterval = 400L
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime

        return elapsedTime >= minInterval
    }


    /**
     * Is online.
     *
     * @return True, if device is having a Internet connection available.
     */
    fun isNetworkAvailable(content: View?): Boolean {
        val cm = content?.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting) {
            return true
        } else {

            showSnackbar(content, content.context.getString(R.string.no_internet_message))
        }
        return false
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun checkPermission(context: Fragment): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context.context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_READ_EXTERNAL_STORAGE)
                return false
            }
        }
        return true
    }


    @Synchronized
    fun getValue(key: String, defaultValue: String, context: Context?): String? {
        if (context == null) return null
        val prefs = SharedPreferencesEncryption(context)
        return prefs.getString(key, defaultValue)
    }

    /*
    *  Getting the real path of image through camera
    * */
    @Synchronized
    fun getOriginalImagePath(fragmentActivity: FragmentActivity): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = fragmentActivity.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null)
        val column_index_data = cursor!!
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToLast()

        return cursor.getString(column_index_data)
    }

    fun getPathFromURI(context: Context, uri: Uri?): String {
        if (uri == null) {
            throw NullPointerException("Uri must not be null")
        }
        val path: String
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            val column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            path = cursor.getString(column_index)
            cursor.close()
            return path
        }
        return uri.path
    }

    /**
     ************** invalidate device token after logout ****************
     */

    /**
     ************ clear tray pending notifictions after logout **************
     */
    fun clearNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    /**
     * This method will hide or show the password
     *
     */

}