package com.app.tourguide.ui.language

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tourguide.R
import com.app.tourguide.activity.MainActivity
import com.app.tourguide.base_classes.BaseFragment
import com.app.tourguide.callBack.ItemSelectedListener
import com.app.tourguide.offlineWork.FileAdapterHebrew
import com.app.tourguide.ui.downloadPreview.AvailLangViewModel
import com.app.tourguide.ui.downloadPreview.pojomodel.DataAvailLang
import com.app.tourguide.ui.videoView.VideoViewFragment
import com.app.tourguide.utils.Constants
import com.app.tourguide.utils.security.ApiFailureTypes
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.Request
import com.tonyodev.fetch2.Status
import com.tonyodev.fetch2core.*
import kotlinx.android.synthetic.main.fragment_choose_language.*
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils
import timber.log.Timber
import java.io.File
import java.util.*


class ChooseLanguageFragment : BaseFragment(), ItemSelectedListener, FetchObserver<Download> {


    private var mViewModel: AvailLangViewModel? = null
    private var fileAdapterHebrew: FileAdapterHebrew? = null


    var packageid: String = ""
    var availLangList: ArrayList<DataAvailLang> = ArrayList()

    var imgUrlList: ArrayList<String> = ArrayList()
    var selectedLang: Int = 1
    var isAppLang: Boolean? = false
    var count: Int = 1

    var languageToLoad = "en"


    private var request: Request? = null
    private var fetch: Fetch? = null
    private val STORAGE_PERMISSION_CODE = 100

    private var downloadedPosition: Int = -1
    private fun hitApiToGetAvailLang() {
        if (!deviceToken().equals("") && !packageid.equals(""))
            mViewModel!!.getAvailLang(packageid, deviceToken())
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(AvailLangViewModel::class.java)
        return inflater.inflate(R.layout.fragment_choose_language, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView_lang.layoutManager = LinearLayoutManager(activity)
        getBundleArguments()

        fetch = Fetch.getDefaultInstance()
    }


    private fun enqueueDownload(url: String) {
        val url = url
        val filePath = getSaveDir() + "/movies/" + getNameFromUrl(url)

        request = Request(url, filePath)
        request!!.extras = getExtrasForRequest(request!!)

        fetch!!.attachFetchObserversForDownload(request!!.id, this)
                .enqueue(request!!, Func { result -> request = result }, Func { result -> Timber.d("SingleDownloadActivity Error: %1\$s", result.toString()) })
    }


    fun getSaveDir(): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/fetch"
    }


    internal fun getNameFromUrl(url: String): String {
        return Uri.parse(url).lastPathSegment
    }


    override fun onChanged(download: Download, reason: Reason) {
        //update view here
        if (request!!.id == download.id) {
            if (reason == Reason.DOWNLOAD_QUEUED || reason == Reason.DOWNLOAD_COMPLETED) {
                // setTitleView(download.file)
            }

            availLangList[downloadedPosition].progress = download.progress

            if (download.status == Status.COMPLETED) {
                availLangList[downloadedPosition].status = getString(R.string.txt_view)
            }

            fileAdapterHebrew?.notifyItemChanged(downloadedPosition)


        }
    }


    private fun getExtrasForRequest(request: Request): Extras {
        val extras = MutableExtras()
        extras.putBoolean("testBoolean", true)
        extras.putString("testString", "test")
        extras.putFloat("testFloat", java.lang.Float.MIN_VALUE)
        extras.putDouble("testDouble", java.lang.Double.MIN_VALUE)
        extras.putInt("testInt", Integer.MAX_VALUE)
        extras.putLong("testLong", java.lang.Long.MAX_VALUE)
        return extras
    }

    private fun getBundleArguments() {
        if (arguments != null) {
            if (arguments?.getBoolean(Constants.PREVIEW_STATUS)!!) {
                checkPermissions()
                isAppLang = false
                ll_app_lang.visibility = View.GONE
                tv_eng_start.visibility = View.VISIBLE
                tv_heb_donwload.visibility = View.VISIBLE
                tv_spanish_download.visibility = View.VISIBLE
                tv_lng_title.text = ""
                packageid = arguments?.getString(Constants.PACKAGE_ID)!!
                recyclerView_lang.visibility = View.VISIBLE
                //fileAdapterHebrew?.notifyDataSetChanged()

                activity?.runOnUiThread {
                    hitApiToGetAvailLang()
                }

            } else {
                updateView()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun attachObservers() {
        mViewModel?.respActivLang?.observe(this, Observer {
            it?.let {
                // if (isAppLang!!) {
                if (it.status == 1) {
                    showSnackBar(it.message!!)
                    activity?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    count++
                    if (count == 2) {
                        val intent = Intent(activity, MainActivity::class.java)
                        activity!!.startActivity(intent)
                    }
                    //replaceFragment(AvailableLocationFragment(), true, R.id.container_main)
                }
            }
        })

        mViewModel?.response?.observe(this, Observer {
            it?.let {
                if (!isAppLang!!) {
                    if (it.status == 1) {
                        availLangList = it.data as ArrayList<DataAvailLang>
                        availLangList.forEachIndexed { index, e ->
                            imgUrlList.add(availLangList.get(index).videoUrl!!)
                        }

                        fileAdapterHebrew = FileAdapterHebrew(availLangList, activity)
                        fileAdapterHebrew?.ItemSelectedListener(this)
                        recyclerView_lang.adapter = fileAdapterHebrew
                    }
                    //fileAdapterHebrew?.notifyDataSetChanged()
                }
            }
        })


        mViewModel?.apiError?.observe(this, Observer {
            it?.let {
                showSnackBar(it)
            }
        })

        mViewModel?.isLoading?.observe(this, Observer {
            it?.let { showLoading(it) }
        })

        mViewModel?.onFailure?.observe(this, android.arch.lifecycle.Observer {
            it?.let {
                showSnackBar(ApiFailureTypes().getFailureMessage(it))
            }
        })
    }

    private fun updateActiveLang(type: String) {
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)

        if (!deviceToken().equals(""))
            mViewModel!!.updateActivLang(type, deviceToken())
    }

    fun updateView() {
        isAppLang = true
        attachObservers()
        tv_eng_start.visibility = View.GONE
        tv_heb_donwload.visibility = View.GONE
        tv_spanish_download.visibility = View.GONE
        tv_lng_title.text = getString(R.string.txt_app_lng)
        recyclerView_lang.adapter = null
        recyclerView_lang.visibility = View.GONE
        ll_app_lang.visibility = View.VISIBLE

        ll_english.setOnClickListener {
            selectedLang = 1
            languageToLoad = "en"
            updateActiveLang(selectedLang.toString())
        }

        ll_hebrew.setOnClickListener {
            selectedLang = 2
            languageToLoad = "iw"
            updateActiveLang(selectedLang.toString())
        }

        ll_spanish.setOnClickListener {
            selectedLang = 3
            languageToLoad = "es"
            updateActiveLang(selectedLang.toString())
        }
    }

    private fun checkPermissions() {
        Dexter.withActivity(activity)
                .withPermissions(
                        PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                        PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                        attachObservers()
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>,
                                                                    token: PermissionToken) {/* ... */
                    }
                }).check()
    }


    override fun onPause() {
        super.onPause()
        if (fetch != null) {
            fetch!!.close()
//            fetch!!.removeListener(fetchListener)
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (fetch != null)
            fetch!!.close()
    }


    override fun selectedItem(pos: Int, type: String, uri: Uri) {
        /*val url: ArrayList<String> = ArrayList()
        url.add(imgUrlList.get(pos))
        DataHeb.addRequestUrl(url)*/

        if (type == Constants.VIEW_VIDEO) {
            val args: Bundle? = Bundle()
            args?.apply {
                putString("video_url", uri.toString())
            }
            val fragment = VideoViewFragment()
            fragment.arguments = args
            addFragment(fragment, true, R.id.container_main)

        } else {
            downloadedPosition = pos
            enqueueDownload(availLangList[pos].videoUrl.toString())
        }
    }

}
