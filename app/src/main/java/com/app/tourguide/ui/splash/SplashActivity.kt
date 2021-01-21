package com.app.tourguide.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.app.tourguide.R
import com.app.tourguide.activity.MainActivity
import com.app.tourguide.base_classes.BaseActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_splash.*
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils

class SplashActivity : BaseActivity() {

    private var mHandler: Handler? = null
    override fun getID(): Int {
        return R.layout.splash_activity
    }

    override fun iniView(savedInstanceState: Bundle?) {
        Picasso.get().load(R.mipmap.splash).into(ivSplash)
        checkPermissions()
    }


    private fun checkPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        PermissionUtils.Manifest_ACCESS_FINE_LOCATION,
                        PermissionUtils.Manifest_ACCESS_COARSE_LOCATION,
                        PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                        PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                        setUpData()
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>,
                                                                    token: PermissionToken) {/* ... */
                    }
                }).check()

    }

    private fun setUpData() {
        mHandler = Handler()
        mHandler?.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

    }


}
