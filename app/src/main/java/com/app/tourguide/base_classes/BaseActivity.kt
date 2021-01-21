package com.app.tourguide.base_classes

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import com.app.tourguide.R
import com.app.tourguide.utils.Constants
import com.app.tourguide.utils.Utils
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission
import java.io.File

/**
 * Created by ${Shubham} on 12/25/2018.
 */
@SuppressLint("Registered")
abstract class BaseActivity : ActivityManagePermission() {
    protected var mDoubleBackToExitPressedOnce = false

    private val PERMISSION_REQUEST = 121

    protected val TAG: String = javaClass.simpleName

    private var mProgressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getID())
        iniView(savedInstanceState)
    }


    abstract fun getID(): Int;
    abstract fun iniView(savedInstanceState: Bundle?);


    fun showSnackBar(message: String, content: View) {
        this.let {
            Snackbar.make(content, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // pass activity's  result to the fragments
        val fragment = supportFragmentManager.findFragmentById(R.id.container_main)
        fragment?.onActivityResult(requestCode, resultCode, data)

    }

    override fun onPause() {
        super.onPause()
        Utils.hideKeyboard(this)
    }

    override fun onStart() {
        super.onStart()
        Utils.hideKeyboard(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragment = supportFragmentManager.findFragmentById(R.id.container_main)
        fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun replaceFragment(fragment: Fragment, container: Int) {
        val tag: String = fragment::class.java.simpleName
        val transaction = supportFragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out)
        transaction?.replace(container, fragment, tag)
                ?.commitAllowingStateLoss()
    }


    /**
     * Add fragment with or without addToBackStack
     *
     * @param fragment which needs to be attached
     * @param addToBackStack is fragment needed to backstack
     */
    fun addFragment(fragment: Fragment, addToBackStack: Boolean, container_id: Int) {
        val tag = fragment.javaClass.simpleName
        val fragmentManager = this.supportFragmentManager
        val fragmentOldObject = fragmentManager?.findFragmentByTag(tag)
        val transaction = fragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in_reverse, R.anim.anim_out_reverse)
        if (fragmentOldObject != null) {
            fragmentManager.popBackStackImmediate(tag, 0)
        } else {
            if (addToBackStack) {
                transaction?.addToBackStack(tag)
            }
            transaction?.add(container_id, fragment, tag)?.commit()
        }
    }


    fun showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = Dialog(this, android.R.style.Theme_Translucent)
            mProgressDialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            mProgressDialog?.setContentView(R.layout.loader_half__layout)
            mProgressDialog?.setCancelable(false)

        }
        mProgressDialog!!.show()
    }

    fun hideProgress() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    fun showLoading(show: Boolean?) {
        if (show!!) showProgress() else hideProgress()
    }


    /**
     ************** start app detail activity to enable disabled permissions ***********
     */

    fun enablePermission() {
        val packageName = packageName

        try {
            //Open the specific App Info page:
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, PERMISSION_REQUEST)
        } catch (e: ActivityNotFoundException) {
            //e.printStackTrace();
            //Open the generic Apps page:
            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            startActivityForResult(intent, PERMISSION_REQUEST)
        }

    }

    /**
     ************ clear images stored in folder bestyme of map snaps *******
     */
    fun clearCacheImages() {
        if (checkForStoragePermission()) {
            val directoryName = Environment.getExternalStorageDirectory().toString() + Constants.APP_HIDDEN_FOLDER
            val mapSnapDirectory = File(directoryName)
            val list = mapSnapDirectory.listFiles()

            if (list != null) {
                for (tempFile in list) {
                    tempFile.delete()
                }
            }
        }

    }


    fun checkForStoragePermission(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                return false
            }
        }
        return true
    }

    //use later
    fun requestStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE), Constants.PERMISSION_REQUEST_CODE)
                return false
            }
        }
        return true
    }


}