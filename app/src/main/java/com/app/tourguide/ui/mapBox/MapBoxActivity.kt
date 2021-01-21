package com.app.tourguide.ui.mapBox

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.graphics.Typeface.DEFAULT
import android.graphics.Typeface.create
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.app.tourguide.R
import com.app.tourguide.activity.MainActivity
import com.app.tourguide.database.DatabaseClient
import com.app.tourguide.database.entity.PackageSpots
import com.app.tourguide.listeners.LocationCallBack
import com.app.tourguide.listeners.locationListener
import com.app.tourguide.listeners.onItemClickedListener
import com.app.tourguide.offlineWork.ActionListener
import com.app.tourguide.offlineWork.DataHeb
import com.app.tourguide.receiver.GpsReceiver
import com.app.tourguide.ui.mapBox.response.DataItem
import com.app.tourguide.ui.mapBox.response.MapData
import com.app.tourguide.ui.mapBox.response.PackageSpotsResponse
import com.app.tourguide.ui.mapBox.response.Route
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.directions.DirectionsCriteria
import com.mapbox.directions.MapboxDirections
import com.mapbox.directions.service.models.DirectionsResponse
import com.mapbox.directions.service.models.DirectionsRoute
import com.mapbox.directions.service.models.Waypoint
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.offline.*
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.Downloader
import com.tonyodev.fetch2core.Func
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import kotlinx.android.synthetic.main.activity_map_box.*
import kotlinx.android.synthetic.main.content_play_audio.*
import kotlinx.android.synthetic.main.navigation_view.*
import kotlinx.android.synthetic.main.play_tour_video.*
import org.json.JSONObject
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MapBoxActivity : AppCompatActivity(), PermissionsListener, onItemClickedListener, locationListener {


    // UI elements
    private var mapView: MapView? = null
    private var map: MapboxMap? = null
    private var progressBar: ProgressBar? = null
    private var downloadButton: Button? = null
    private var listButton: Button? = null

    private var isEndNotified: Boolean = false
    private var regionSelected: Int = 0

    // Offline objects
    private var offlineManager: OfflineManager? = null
    private var offlineRegion: OfflineRegion? = null
    private var permissionsManager: PermissionsManager? = null

    private var rvSpotList: RecyclerView? = null
    private var spotListAdapter: SpotListAdapter? = null
    private var mViewModel: MapBoxViewModel? = null

    internal var LOG_TAG = "DIRECTIO"
    private var currentRoute: DirectionsRoute? = null

    private var mRegionToDownload: MapData? = null
    private var mRegionRegionSpot: List<DataItem>? = null
    private var mRegionRoute: Route? = null

    private val gson = Gson()
    private var packageSpotsListStr: String = ""

    private var fetch: Fetch? = null
    private var actionListener: ActionListener? = null
    private var dataSourceFac: DataSource.Factory? = null
    private lateinit var mRegionPackageId: String

    private var markerView: MarkerView? = null
    private var markerViewManager: MarkerViewManager? = null

    private var MAP_NORMAL: String = "normal"
    private var MAP_SATELLITE: String = "satellite"
    private lateinit var locationEngine: LocationEngine
    val DEFAULT_INTERVAL_IN_MILLISECONDS = 10000L
    //val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

    private val callback = LocationListeningCallback(this)

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var fragment: Fragment
    private lateinit var iv_toggle_menu: ImageView


    private lateinit var mTourTimeStatus: String
    private lateinit var points: ArrayList<LatLng>
    /**
     * mTourTokenStatus denotes tour active or inactive
     * true token is active
     * false toke inactive
     */
    private var mTourTokenStatus: String = "active"
    private lateinit var mTourDisTravelledByUser: String
    private val mVideoWatchedList = mutableListOf<String>()
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_key))

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_map_box)

        mViewModel = ViewModelProviders.of(this).get(MapBoxViewModel::class.java)
        mapView = findViewById(R.id.mapView)
        rvSpotList = findViewById(R.id.rvSpotList)


        callback.onLocationChangeListener(this)
        attachObserver()
        clickListeners()
        getBundleArguments()
        doLocationRequest()
        navigationDrawer()
        attachLocationOnOffListener()



        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync { mapboxMap ->
            map = mapboxMap
            mapboxMap.setStyle(Style.SATELLITE) { style ->

                // Assign progressBar for later use
                progressBar = findViewById(R.id.progress_bar)

                // Set up the offlineManager
                offlineManager = OfflineManager.getInstance(this@MapBoxActivity)

                markerViewManager = MarkerViewManager(mapView, mapboxMap)

                enableLocationComponent(style)

                if (isLocationEnabled(this@MapBoxActivity)) {
                    if (isNetworkAvailable()) {
                        mViewModel?.getPackagesData(mRegionPackageId, deviceToken())
                    } else {
                        RetrievePackageSpots(baseContext, mRegionPackageId, false).execute()
                    }
                } else {
                    showLocationDialog()
                }


                downloadButton = this.findViewById(R.id.download_button)
                downloadButton!!.setOnClickListener { view -> downloadRegionDialog() }

                // List offline regions
                listButton = findViewById(R.id.list_button)
                listButton!!.setOnClickListener { view -> downloadedRegionList() }
            }
        }

    }

    //45 min=2700000
    private fun startTripTimeTracker(tourTime: Int): CountDownTimer {
        return object : CountDownTimer(tourTime.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                print("${TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)}")
            }

            override fun onFinish() {
                //trip limit end
                mTourTokenStatus = "inactive"
            }
        }
    }

    override fun onLocationChanged(lat: Double, lng: Double) {
        if (mRegionToDownload != null) {
            if (mRegionToDownload?.boothLat != "" && mRegionToDownload?.boothLong != "") {
                val distance = meterDistanceBtBoothAndUser(
                        mRegionToDownload?.boothLat!!.toFloat(),
                        mRegionToDownload?.boothLong!!.toFloat(),
                        lat.toFloat(), lng.toFloat())

                if (mRegionToDownload?.maxDistance != "" && mRegionToDownload?.minDistance != "") {
                    //token inactive if cross tour time limit and distance greater than max distance
                    if (mTourTokenStatus == "inactive" && distance > mRegionToDownload?.maxDistance!!.toFloat()) {
                        takeUserToHomeScreen()
                    }

                    if (mTourTokenStatus == "inactive" && distance < mRegionToDownload?.minDistance!!.toFloat()) {
                        //If the user is still inside the 50m range from the ticket booth and the tour is still running inside the app,
                        // the user can still use the tour normally, including watching all videos.
                    }

                    if (mTourTokenStatus == "inactive" && distance > mRegionToDownload?.minDistance!!.toFloat()) {
                        //If the user tablet location becomes more then 50 meters away from the ticket booth with an inactivated token, then the tour
                        // will automatically finish and the app will return to the home screen. To see the tour again, a new token should be used.
                        takeUserToHomeScreen()
                    }


                    //VIDEO WILL BE PLAY AUTOMATICALLY IF USER COMES INSIDE 20METER RANGE OF SPOTS
                    mRegionRegionSpot!!.forEachIndexed { index, location ->
                        if (20 > meterDistanceBtBoothAndUser(location.latitude!!.toFloat(), location.longitude!!.toFloat(), lat.toFloat(), lng.toFloat())) {
                            if (mVideoWatchedList.size > 0) {
                                if (mVideoWatchedList.binarySearch(location.id.toString()) == -1) {
                                    val downloadData = SpotListAdapter.downloads[index]
                                    val file = File(downloadData.download!!.file)
                                    var uri1: Uri? = null
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        uri1 = Uri.parse(file.path)
                                    } else {
                                        uri1 = Uri.fromFile(file)
                                    }
                                    playVideo(uri1.toString())
                                    mVideoWatchedList.add(location.id.toString())
                                }
                            }
                        }
                    }

                } else {
                    showMessage("minimum and maximum distance of tour not found")
                }


            } else {
                showMessage("Booth location not found")
            }
        }
    }


    fun meterDistanceBtBoothAndUser(lat_booth: Float, lng_booth: Float, lat_user: Float, lng_user: Float): Double {
        val pk = (180f / Math.PI).toFloat()
        val a1 = lat_booth / pk
        val a2 = lng_booth / pk
        val b1 = lat_user / pk
        val b2 = lng_user / pk

        val t1 = Math.cos(a1.toDouble()) * Math.cos(a2.toDouble()) * Math.cos(b1.toDouble()) * Math.cos(b2.toDouble())
        val t2 = Math.cos(a1.toDouble()) * Math.sin(a2.toDouble()) * Math.cos(b1.toDouble()) * Math.sin(b2.toDouble())
        val t3 = Math.sin(a1.toDouble()) * Math.sin(b1.toDouble())
        val tt = Math.acos(t1 + t2 + t3)

        return 6366000 * tt
    }

    private fun takeUserToHomeScreen() {
        startActivity(Intent(this@MapBoxActivity, MainActivity::class.java))
        finish()
    }

    private fun attachLocationOnOffListener() {
        registerReceiver(GpsReceiver(object : LocationCallBack {
            override fun onLocationTriggered() {
                if (isLocationEnabled(this@MapBoxActivity)) {
                    //location enable
                } else {
                    //location disable
                    showLocationDialog()
                }
            }
        }), IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    private fun showLocationDialog() {
        val alertDialog: AlertDialog? = this.let {
            val builder = AlertDialog.Builder(it)
            builder.setCancelable(false)
            builder.setMessage(getString(R.string.txt_enable_location))

            builder.apply {
                setPositiveButton("Ok"
                ) { dialog, id ->
                    if (!isLocationEnabled(this@MapBoxActivity)) {
                        showLocationDialog()
                    }
                }
            }
            builder.create()
        }

        alertDialog!!.show()
    }

    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(context.contentResolver, Settings.Secure.LOCATION_MODE)
            return !TextUtils.isEmpty(locationProviders)
        }
    }

    private fun navigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.design_navigation_view)
        iv_toggle_menu = findViewById(R.id.iv_toggle_menu)

        tv_language.setOnClickListener {
            mDrawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this@MapBoxActivity, MainActivity::class.java))
            finish()
        }

        tv_tour_list.setOnClickListener {
            mDrawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this@MapBoxActivity, MainActivity::class.java))
            finish()
        }


        mDrawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(p0: Int) {
            }

            override fun onDrawerSlide(p0: View, p1: Float) {
            }

            override fun onDrawerClosed(p0: View) {
                iv_toggle_menu.visibility = View.VISIBLE
            }

            override fun onDrawerOpened(p0: View) {

                DrawableCompat.setTint(iv_closedrawer.drawable, ContextCompat.getColor(this@MapBoxActivity, R.color.black))
                iv_toggle_menu.visibility = View.INVISIBLE
            }
        })

        iv_toggle_menu.setOnClickListener {
            mDrawerLayout.bringToFront()
            if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START)
                iv_toggle_menu.visibility = View.VISIBLE
            } else {
                DrawableCompat.setTint(iv_closedrawer.drawable, ContextCompat.getColor(this@MapBoxActivity, R.color.black))
                iv_toggle_menu.visibility = View.INVISIBLE
                mDrawerLayout.openDrawer(GravityCompat.START)
            }
        }

        iv_closedrawer.setOnClickListener {
            mDrawerLayout.closeDrawer(GravityCompat.START)
            iv_toggle_menu.visibility = View.VISIBLE
        }

        iv_toggle_menu.visibility = View.VISIBLE

    }


    private fun doLocationRequest() {
        //location request
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .setDisplacement(1F)
                .build()
        locationEngine.requestLocationUpdates(request, callback, mainLooper)
        locationEngine.getLastLocation(callback)


    }


    private class LocationListeningCallback internal constructor(activity: MapBoxActivity) : LocationEngineCallback<LocationEngineResult> {
        private val activityWeakReference: WeakReference<MapBoxActivity>

        init {
            this.activityWeakReference = WeakReference(activity)
        }

        private lateinit var listener: locationListener
        fun onLocationChangeListener(listener: locationListener) {
            this.listener = listener
        }


        override fun onSuccess(result: LocationEngineResult?) {
            // The LocationEngineCallback interface's method which fires when the device's location has changed.
            result?.lastLocation
            this.listener.onLocationChanged(result?.lastLocation?.latitude!!, result.lastLocation?.longitude!!)
            Log.d("Location=", "Location=${result?.lastLocation}")

        }

        override fun onFailure(exception: Exception) {
            // The LocationEngineCallback interface's method which fires when the device's location can not be captured
            Log.d("Location=", "${exception.message}")
        }

    }

    @SuppressLint("MissingPermission")
    private fun clickListeners() {

/*        ivMenu.setOnClickListener {
            startActivity(Intent(this@MapBoxActivity, MainActivity::class.java))
            finish()
        }*/

        ivMapStyle.setOnClickListener {
            if (ivMapStyle.tag.equals(MAP_NORMAL)) {
                map?.setStyle(Style.SATELLITE)
                ivMapStyle.tag = MAP_SATELLITE
                ivMapStyle.setBackgroundResource(R.drawable.ic_satellite)
            } else if (ivMapStyle.tag.equals(MAP_SATELLITE)) {
                map?.setStyle(Style.LIGHT)
                ivMapStyle.tag = MAP_NORMAL
                ivMapStyle.setBackgroundResource(R.drawable.ic_map)
            }
        }

        ivNavigate.setOnClickListener {
            if (map!!.locationComponent.lastKnownLocation != null)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(map!!.locationComponent.lastKnownLocation?.latitude!!, map!!.locationComponent.lastKnownLocation?.longitude!!), 13.0))
        }
    }

    private fun getBundleArguments() {
        mRegionPackageId = intent.getStringExtra("packageId")

        val fetchConfiguration = FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(4)
                .setHttpDownloader(OkHttpDownloader(Downloader.FileDownloaderType.PARALLEL))
                .setNamespace(FETCH_NAMESPACE)
                //.setNotificationManager(DefaultFetchNotificationManager(this))
                .build()
        fetch = Fetch.getInstance(fetchConfiguration)
        fetch!!.setGlobalNetworkType(NetworkType.ALL)
    }

    private fun enqueueDownloads() {
        val requests = DataHeb.getFetchRequestWithGroupId(mRegionPackageId.hashCode())
        fetch!!.enqueue(requests, Func {
        })
    }


    private fun attachObserver() {
        mViewModel?.response?.observe(this, Observer { it ->
            it.let {
                if (it!!.status == 1) {
                    mRegionRegionSpot = it.data as List<DataItem>
                    mRegionToDownload = it.mapData
                    mRegionRoute = it.route
                    packageSpotsListStr = gson.toJson(it)
                    downloadTourPackageRegion(it.mapData?.packageId, it.mapData?.regionName)
                } else {
                    showMessage(it.message!!)
                }
            }
        })

        mViewModel?.apiError?.observe(this, Observer {
            it?.let {
                showMessage(it)
                //showSnackBar(it)
            }
        })

        mViewModel?.isLoading?.observe(this, Observer {
            it?.let {
                //showMessage(it)
                //showLoading(it)
            }
        })

        mViewModel?.onFailure?.observe(this, Observer {
            it?.let {
                //showMessage(it)
                //showSnackBar(ApiFailureTypes().getFailureMessage(it))
            }
        })
    }


    fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting) {
            return true
        }
        return false
    }


    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            val locationComponent = map!!.locationComponent

            // Activate with options
            locationComponent.activateLocationComponent(this, loadedMapStyle)

            // Enable to make component visible
            locationComponent.isLocationComponentEnabled = true

            // Set the component's camera mode
            //locationComponent.cameraMode = CameraMode.TRACKING

            // Set the component's render mode
            //locationComponent.renderMode = RenderMode.COMPASS
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(this)
        }

    }


    // Override Activity lifecycle methods
    public override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    //
    override fun onStop() {
        super.onStop()
        locationEngine.removeLocationUpdates(callback)
        mapView!!.onStop()
    }

    public override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (markerViewManager != null) {
            markerViewManager?.onDestroy()
        }
        fetch?.close()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    fun deviceToken(): String {
        return Settings.Secure.getString(this?.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun downloadRegionDialog() {
        // Set up download interaction. Display a dialog
        // when the user clicks download button and require
        // a user-provided region name
        val builder = AlertDialog.Builder(this@MapBoxActivity)

        val regionNameEdit = EditText(this@MapBoxActivity)
        regionNameEdit.hint = getString(R.string.set_region_name_hint)

        // Build the dialog box
        builder.setTitle(getString(R.string.dialog_title))
                .setView(regionNameEdit)
                .setMessage(getString(R.string.dialog_message))
                .setPositiveButton(getString(R.string.dialog_positive_button)) { dialog, which ->
                    val regionName = regionNameEdit.text.toString()
                    // Require a region name to begin the download.
                    // If the user-provided string is empty, display
                    // a toast message and do not begin download.
                    if (regionName.length == 0) {
                        Toast.makeText(this@MapBoxActivity, getString(R.string.dialog_toast), Toast.LENGTH_SHORT).show()
                    } else {
                        // Begin download process
                        //downloadRegion(regionName)
                    }
                }
                .setNegativeButton(getString(R.string.dialog_negative_button)) { dialog, which -> dialog.cancel() }

        //Display the dialog
        builder.show()
    }

    private fun downloadRegion(packageId: String?, pckRegionName: String?) {
        // Define offline region parameters, including bounds,
        // min/max zoom, and metadata

        // Start the progressBar
        startProgress()

        // Create offline definition using the current
        // style and boundaries of visible map area
        val styleUrl = map!!.style!!.url

        //LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

        val bounds = LatLngBounds.Builder()
                .include(LatLng(mRegionToDownload?.minLatitude!!.toDouble(), mRegionToDownload?.minLongitude!!.toDouble())) // Northeast
                .include(LatLng(mRegionToDownload?.maxLatitude!!.toDouble(), mRegionToDownload?.maxLongitude!!.toDouble())) // Southwest
                /*.include(LatLng(30.7168, 76.7474)) // Northeast
                .include(LatLng(30.7307, 76.7785)) // Southwest*/
                .build()

        val minZoom = mRegionToDownload?.minZoom!!.toDouble()
        val maxZoom = mRegionToDownload?.maxZoom!!.toDouble()
        /*val minZoom = map!!.getCameraPosition().zoom
        val maxZoom = map!!.getMaxZoomLevel()*/
        val pixelRatio = this.resources.displayMetrics.density

        val definition = OfflineTilePyramidRegionDefinition(styleUrl, bounds, minZoom, maxZoom, pixelRatio)

        // Build a JSONObject using the user-defined offline region title,
        // convert it into string, and use it to create a metadata variable.
        // The metadata variable will later be passed to createOfflineRegion()
        var metadata: ByteArray?
        try {
            val jsonObject = JSONObject()
            jsonObject.put(JSON_FIELD_REGION_NAME, pckRegionName)
            jsonObject.put(JSON_FIELD_REGION_ID, packageId)
            val json = jsonObject.toString()
            metadata = json.toByteArray(charset(JSON_CHARSET))
        } catch (exception: Exception) {
            Timber.e("Failed to encode metadata: %s", exception.message)
            metadata = null
        }

        // Create the offline region and launch the download
        offlineManager!!.createOfflineRegion(definition, metadata!!, object : OfflineManager.CreateOfflineRegionCallback {
            override fun onCreate(offlineRegion: OfflineRegion) {
                Timber.d("Offline region created: %s", packageId)
                this@MapBoxActivity.offlineRegion = offlineRegion
                launchDownload()
            }

            override fun onError(error: String) {
                Timber.e("Error: %s", error)
            }
        })
    }

    private fun launchDownload() {
        // Set up an observer to handle download progress and
        // notify the user when the region is finished downloading
        offlineRegion!!.setObserver(object : OfflineRegion.OfflineRegionObserver {
            override fun onStatusChanged(status: OfflineRegionStatus) {
                // Compute a percentage
                val percentage = if (status.requiredResourceCount >= 0)
                    100.0 * status.completedResourceCount / status.requiredResourceCount
                else
                    0.0

                if (status.isComplete) {
                    // Download complete
                    endProgress(getString(R.string.end_progress_success))
                    //recall the method to load the downloaded region on map
                    downloadTourPackageRegion(mRegionToDownload?.packageId, mRegionToDownload?.regionName)
                    //downloadTourPackageRegion(it.mapData?.packageId, it.mapData?.regionName)
                    return
                } else if (status.isRequiredResourceCountPrecise) {
                    // Switch to determinate state
                    setPercentage(Math.round(percentage).toInt())
                }

                // Log what is being currently downloaded
                Timber.d("%s/%s resources; %s bytes downloaded.",
                        status.completedResourceCount.toString(),
                        status.requiredResourceCount.toString(),
                        status.completedResourceSize.toString())
            }

            override fun onError(error: OfflineRegionError) {
                Timber.e("onError reason: %s", error.reason)
                Timber.e("onError message: %s", error.message)
            }

            override fun mapboxTileCountLimitExceeded(limit: Long) {
                Timber.e("Mapbox tile count limit exceeded: %s", limit)
            }
        })

        // Change the region state
        offlineRegion!!.setDownloadState(OfflineRegion.STATE_ACTIVE)
    }

    private fun downloadTourPackageRegion(packageId: String?, pckRegionName: String?) {
        // Reset the region selected int to 0
        regionSelected = -1
        var status = false
        // Query the DB asynchronously
        offlineManager!!.listOfflineRegions(object : OfflineManager.ListOfflineRegionsCallback {
            override fun onList(offlineRegions: Array<OfflineRegion>?) {
                // Check result. If no regions have beenOfflineManager
                // downloaded yet, notify user and return
                if (offlineRegions == null || offlineRegions.size == 0) {
                    downloadRegion(packageId, pckRegionName)
                    return
                }


                for (offlineRegion in offlineRegions) {
                    regionSelected++
                    //offlineRegionsNames.add(getRegionName(offlineRegion))
                    if (packageId == getRegionName(offlineRegion)) {
                        status = true
                        break
                    }
                }

                if (status) {
                    //region already downloaded
                    //val items = offlineRegionsNames.toTypedArray<CharSequence>()
                    val bounds = offlineRegions[regionSelected].definition.bounds
                    val regionZoom = offlineRegions[regionSelected].definition.minZoom

                    val cameraPosition = CameraPosition.Builder()
                            .target(bounds.center)
                            .zoom(regionZoom)
                            .build()


                    // Move camera to new position
                    map!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    addTourSpotsOnMap()
                    if (isNetworkAvailable()) {
                        RetrievePackageSpots(baseContext, mRegionToDownload?.packageId!!, true).execute()
                    } else {
                        RetrievePackageSpots(baseContext, mRegionToDownload?.packageId!!, false).execute()
                    }

                } else {
                    //download region
                    downloadRegion(packageId, pckRegionName)
                }
            }

            override fun onError(error: String) {
                Timber.e("Error: %s", error)
            }
        })

    }


    private fun createTourRegionMarker(number: String): Bitmap {
        val markerLayout = getLayoutInflater().inflate(R.layout.marker_layout, null)

        val markerImage = markerLayout.findViewById(R.id.marker_image) as ImageView
        val markerNumber = markerLayout.findViewById(R.id.marker_text) as TextView
        markerImage.setImageResource(R.mipmap.icon)

        markerNumber.setText(number)

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight())

        val bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerLayout.draw(canvas)
        return bitmap
    }


    private fun loadDownloadedRegion(packageId: String?, pckRegionName: String?) {
        // Reset the region selected int to 0
        regionSelected = -1
        var status = false
        // Query the DB asynchronously
        offlineManager!!.listOfflineRegions(object : OfflineManager.ListOfflineRegionsCallback {
            override fun onList(offlineRegions: Array<OfflineRegion>?) {
                // Check result. If no regions have beenOfflineManager
                // downloaded yet, notify user and return
                if (offlineRegions == null || offlineRegions.size == 0) {
                    downloadRegion(packageId, pckRegionName)
                    return
                }


                for (offlineRegion in offlineRegions) {
                    regionSelected++
                    //offlineRegionsNames.add(getRegionName(offlineRegion))
                    if (packageId == getRegionName(offlineRegion)) {
                        status = true
                        break
                    }
                }

                if (status) {
                    //region already downloaded
                    //val items = offlineRegionsNames.toTypedArray<CharSequence>()
                    val bounds = offlineRegions[regionSelected].definition.bounds
                    val regionZoom = offlineRegions[regionSelected].definition.minZoom

                    val cameraPosition = CameraPosition.Builder()
                            .target(bounds.center)
                            .zoom(regionZoom)
                            .build()

                    // Move camera to new position
                    map!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                    addTourSpotsOnMap()
                } else {
                    //download region
                    downloadRegion(packageId, pckRegionName)
                }
            }

            override fun onError(error: String) {
                Timber.e("Error: %s", error)
            }
        })


    }

    private fun downloadedRegionList() {
        // Build a region list when the user clicks the list button

        // Reset the region selected int to 0
        regionSelected = 1

        // Query the DB asynchronously
        offlineManager!!.listOfflineRegions(object : OfflineManager.ListOfflineRegionsCallback {
            override fun onList(offlineRegions: Array<OfflineRegion>?) {
                // Check result. If no regions have been
                // downloaded yet, notify user and return
                if (offlineRegions == null || offlineRegions.size == 0) {
                    Toast.makeText(applicationContext, getString(R.string.toast_no_regions_yet), Toast.LENGTH_SHORT).show()
                    return
                }

                // Add all of the region names to a list
                val offlineRegionsNames = ArrayList<String>()
                for (offlineRegion in offlineRegions) {
                    offlineRegionsNames.add(getRegionName(offlineRegion))
                }
                val items = offlineRegionsNames.toTypedArray<CharSequence>()


                val bounds = offlineRegions[regionSelected].definition.bounds
                val regionZoom = offlineRegions[regionSelected].definition.minZoom


                //getRoute(origin, destination);
                // Create new camera position
                val cameraPosition = CameraPosition.Builder()
                        .target(bounds.center)
                        .zoom(regionZoom)
                        .build()

                // Move camera to new position
                map!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                addTourSpotsOnMap()

            }

            override fun onError(error: String) {
                Timber.e("Error: %s", error)
            }
        })
    }

    private fun getRoute(origin: Waypoint, destination: Waypoint) {
        val md = MapboxDirections.Builder()
                .setAccessToken(getString(R.string.mapbox_key))
                .setOrigin(origin)
                .setDestination(destination)
                .setProfile(DirectionsCriteria.PROFILE_WALKING)
                .build()

        md.enqueue(object : Callback<DirectionsResponse> {
            override fun onResponse(response: Response<DirectionsResponse>, retrofit: Retrofit) {
                // You can get generic HTTP info about the response
                Log.d(LOG_TAG, "Response code: " + response.code())

                // Print some info about the route
                currentRoute = response.body().routes[0]
                Log.d(LOG_TAG, "Distance: " + currentRoute!!.distance)
                showMessage(String.format("Route is %d meters long.", currentRoute!!.distance))

                // Draw the route on the map
                //drawRoute(currentRoute);
            }

            override fun onFailure(t: Throwable) {
                Log.e(LOG_TAG, "Error: " + t.message)
                showMessage("Error: " + t.message)
            }
        })
    }

    private fun drawRoute(route: DirectionsRoute) {
        // Convert List<Waypoint> into LatLng[]
        //        List<Waypoint> waypoints = route.getGeometry().getWaypoints();
        //        LatLng[] point = new LatLng[waypoints.size()];
        //        for (int i = 0; i < waypoints.size(); i++) {
        //            point[i] = new LatLng(
        //                    waypoints.get(i).getLatitude(),
        //                    waypoints.get(i).getLongitude());
        //        }
        //
        //        // Draw Points on MapView
        //        map.addPolyline(new PolylineOptions()
        //                .add(point)
        //                .color(Color.parseColor("#3887be"))
        //                .width(5));
    }


    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getRegionName(offlineRegion: OfflineRegion): String {
        // Get the region name from the offline region metadata
        var regionName: String

        try {
            val metadata = offlineRegion.metadata
            val json = String(metadata, charset(JSON_CHARSET))
            val jsonObject = JSONObject(json)
            regionName = jsonObject.getString(JSON_FIELD_REGION_ID)
        } catch (exception: Exception) {
            Timber.e("Failed to decode metadata: %s", exception.message)
            regionName = String.format(getString(R.string.region_name), offlineRegion.id)
        }

        return regionName
    }

    // Progress bar methods
    private fun startProgress() {
        // Disable buttons
        downloadButton!!.isEnabled = false
        listButton!!.isEnabled = false

        // Start and show the progress bar
        isEndNotified = false
        progressBar!!.isIndeterminate = true
        progressBar!!.visibility = View.VISIBLE
    }

    private fun setPercentage(percentage: Int) {
        progressBar!!.isIndeterminate = false
        progressBar!!.progress = percentage
    }

    private fun endProgress(message: String) {
        // Don't notify more than once
        if (isEndNotified) {
            return
        }
        // Enable buttons
        downloadButton!!.isEnabled = true
        listButton!!.isEnabled = true

        // Stop and hide the progress bar
        isEndNotified = true
        progressBar!!.isIndeterminate = false
        progressBar!!.visibility = View.GONE

        // Show a toast
        Toast.makeText(this@MapBoxActivity, message, Toast.LENGTH_LONG).show()
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, "Need Permission to show your current location", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun addTourSpotsOnMap() {
        points = ArrayList<LatLng>()

        tvTourName.text = mRegionToDownload?.tourNameLanguage
        map!!.setMaxZoomPreference(mRegionToDownload!!.maxZoom!!.toDouble())
        map!!.setMinZoomPreference(mRegionToDownload!!.minZoom!!.toDouble())


        mRegionRegionSpot!!.forEachIndexed { index, dataItem ->
            points.add(LatLng(dataItem.latitude!!.toDouble(), dataItem.longitude!!.toDouble()))
        }

        //ADD SPOT ON MAP
        for (i in points.indices) {
            val options = MarkerOptions()
            val iconFactory = IconFactory.getInstance(this@MapBoxActivity)
            var index = i
            val icon = iconFactory.fromBitmap(drawTextToBitmap(this@MapBoxActivity, R.drawable.ic_circle, index.plus(1).toString()))
            options.icon = icon
            options.position = points[i]
            map!!.addMarker(options)
        }

        //ATTACHED CLICKED LISTENER ON MARKER CLICK
        map!!.setOnMarkerClickListener {
            points.forEachIndexed { index, latLng ->
                if (latLng.equals(it.position)) {
                    val downloadData = SpotListAdapter.downloads[index]
                    val file = File(downloadData.download!!.file)
                    var uri1: Uri? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        uri1 = Uri.parse(file.path)
                    } else {
                        uri1 = Uri.fromFile(file)
                    }
                    playVideo(uri1.toString())
                }
            }
            false
        }

        if (mRegionToDownload?.tourTime!!.isNotEmpty()) startTripTimeTracker(mRegionToDownload?.tourTime!!.toInt() * 60000).start() else showMessage("Total tour time not available")

        showSpotDetails()
        drawRouteAmongSpots()
    }

    private fun drawRouteAmongSpots() {
        try {
            val coordinates = mRegionRoute?.trips!![0]?.geometry?.coordinates
            val point = ArrayList<LatLng>()

            coordinates!!.forEachIndexed { index, location ->
                point.add(LatLng(
                        location!!.get(0)!!,
                        location.get(1)!!))
            }

            map?.addPolyline(PolylineOptions()
                    .addAll(point)
                    .color(Color.parseColor("#2196F3"))
                    .width(3F)
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@MapBoxActivity, "No route found", Toast.LENGTH_LONG).show()
        }
    }


    fun drawTextToBitmap(gContext: Context,
                         gResId: Int,
                         gText: String): Bitmap {

        val resources = gContext.resources
        val scale = resources.displayMetrics.density
        var bitmap = BitmapFactory.decodeResource(resources, gResId)

        var bitmapConfig: android.graphics.Bitmap.Config? = bitmap.config
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true)

        val canvas = Canvas(bitmap)
        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // text color - #000000
        paint.color = Color.rgb(255, 255, 255)
        // text size in pixels
        paint.textSize = (14 * scale).toInt().toFloat()
        //text bold
        paint.typeface = create(DEFAULT, Typeface.BOLD)
        // text shadow
        //paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)

        // draw text to the Canvas center
        val bounds = Rect()
        paint.getTextBounds(gText, 0, gText.length, bounds)
        val x = (bitmap.width - bounds.width()) / 2
        val y = (bitmap.height + bounds.height()) / 2

        canvas.drawText(gText, x.toFloat(), y.toFloat(), paint)

        return bitmap
    }


    /**
     * file_type will have value either video or audio
     * spot_video_vtt contain the vtt file otherwise it will be empty
     */
    override fun onItemClickListener(pos: Int, uri: String) {
        val data = mRegionRegionSpot?.get(pos)

        //PLAY AUDIO
        if (data?.fileType.equals("audio")) {
            playAudio(uri, data)
        }

        //PLAY VIDEO WITH VTT FILE
        if (data?.fileType.equals("video") && data?.spotVideoVtt != "") {
            playVideoWithSubtitle(uri, data)
        }

        //PLAY VIDEO WITOUT VTT
        if (data?.fileType.equals("video") && data?.spotVideoVtt == "") {
            playVideoWithoutSubtitle(uri)
        }
    }


    fun playAudio(audio: String, data: DataItem?) {
        val mPlayTourSpot = Dialog(this, android.R.style.Theme_Translucent)
        mPlayTourSpot.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        mPlayTourSpot.setContentView(R.layout.content_play_audio)
        mPlayTourSpot.setCancelable(true)

        mPlayTourSpot.setOnDismissListener {
            object : DialogInterface {
                override fun dismiss() {
                    if (nPlayer != null) {
                        nPlayer!!.release()
                        nPlayer = null
                    }
                }

                override fun cancel() {
                    if (nPlayer != null) {
                        nPlayer!!.release()
                        nPlayer = null
                    }
                }

            }
        }

        mPlayTourSpot.tvSpotTitle.text = data?.spotName
        mPlayTourSpot.tvSpotDesc.text = data?.spotDesc
        Picasso.get()
                .load(data!!.vThumbnail)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(mPlayTourSpot.ivSpotImage, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        Picasso.get()
                                .load(data!!.vThumbnail)
                                .into(mPlayTourSpot.ivSpotImage, object : com.squareup.picasso.Callback {
                                    override fun onSuccess() {

                                    }

                                    override fun onError(e: Exception?) {
                                        print("Couldn't fetch data")
                                    }
                                })
                    }
                })


        mPlayTourSpot.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        val player: ExoPlayer
        if (nPlayer != null) {
            nPlayer!!.release()
            nPlayer = null
        }
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, this!!.getString(R.string.app_name)), defaultBandwidthMeter)
        dataSourceFac = dataSourceFactory
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(defaultBandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val contentMediaSource = buildMediaSource(Uri.parse(audio))
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        player.setPlayWhenReady(true)

        mPlayTourSpot.pvSpotAudio.player = player
        player.prepare(contentMediaSource)
        nPlayer = player

        nPlayer?.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                Log.d(TAG, "" + playbackParameters)
            }

            override fun onSeekProcessed() {
                Log.d(TAG, "")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                Log.d(TAG, "" + trackGroups)
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.d(TAG, "" + error!!.message)
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.d(TAG, "loading [$isLoading]")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                Log.d(TAG, "" + reason)
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.d(TAG, "" + repeatMode)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                Log.d(TAG, "" + shuffleModeEnabled)
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                Log.d(TAG, "" + timeline)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    if (nPlayer != null) {
                        nPlayer!!.release()
                        nPlayer = null
                        mPlayTourSpot.dismiss()
                    }
                }
            }
        })

        mPlayTourSpot.show()
    }


    fun playVideoWithSubtitle(video: String, data: DataItem?) {
        val mPlayTourVideo = Dialog(this, android.R.style.Theme_Translucent)
        mPlayTourVideo.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        mPlayTourVideo.setContentView(R.layout.play_tour_video)
        mPlayTourVideo.setCancelable(true)

        mPlayTourVideo.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        val player: ExoPlayer
        if (nPlayer != null) {
            nPlayer!!.release()
            nPlayer = null
        }

        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, this.getString(R.string.app_name)), defaultBandwidthMeter)
        dataSourceFac = dataSourceFactory

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(defaultBandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val contentMediaSource = buildMediaSource(Uri.parse(video))

        val mediaSources = arrayOfNulls<MediaSource>(2) //The Size must change depending on the Uris
        mediaSources[0] = contentMediaSource //uri
        val subtitleSource = SingleSampleMediaSource(Uri.parse(data?.spotVideoVtt),
                dataSourceFactory, Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, Format.NO_VALUE, "en", null),
                C.TIME_UNSET)
        mediaSources[1] = subtitleSource

        val mediaSource = MergingMediaSource(mediaSources[0], mediaSources[1])

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        player.setPlayWhenReady(true)
        mPlayTourVideo.pvTourVideo.player = player
        player.prepare(mediaSource)
        nPlayer = player

        nPlayer?.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                Log.d(TAG, "" + playbackParameters)
            }

            override fun onSeekProcessed() {
                Log.d(TAG, "")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                Log.d(TAG, "" + trackGroups)
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.d(TAG, "" + error!!.message)
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.d(TAG, "loading [$isLoading]")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                Log.d(TAG, "" + reason)
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.d(TAG, "" + repeatMode)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                Log.d(TAG, "" + shuffleModeEnabled)
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                Log.d(TAG, "" + timeline)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    if (nPlayer != null) {
                        nPlayer!!.release()
                        nPlayer = null
                        mPlayTourVideo.dismiss()
                    }
                }
            }
        })

        mPlayTourVideo.show()
    }

    fun playVideoWithoutSubtitle(video: String) {

        val mPlayTourVideo = Dialog(this, android.R.style.Theme_Translucent)
        mPlayTourVideo.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        mPlayTourVideo.setContentView(R.layout.play_tour_video)
        mPlayTourVideo.setCancelable(true)

        mPlayTourVideo.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        val player: ExoPlayer
        if (nPlayer != null) {
            nPlayer!!.release()
            nPlayer = null
        }
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, this!!.getString(R.string.app_name)), defaultBandwidthMeter)
        dataSourceFac = dataSourceFactory
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(defaultBandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val contentMediaSource = buildMediaSource(Uri.parse(video))
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        player.setPlayWhenReady(true)
        mPlayTourVideo.pvTourVideo.player = player
        player.prepare(contentMediaSource)
        nPlayer = player

        nPlayer?.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                Log.d(TAG, "" + playbackParameters)
            }

            override fun onSeekProcessed() {
                Log.d(TAG, "")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                Log.d(TAG, "" + trackGroups)
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.d(TAG, "" + error!!.message)
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.d(TAG, "loading [$isLoading]")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                Log.d(TAG, "" + reason)
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.d(TAG, "" + repeatMode)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                Log.d(TAG, "" + shuffleModeEnabled)
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                Log.d(TAG, "" + timeline)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    if (nPlayer != null) {
                        nPlayer!!.release()
                        nPlayer = null
                        mPlayTourVideo.dismiss()
                    }
                }
            }
        })

        mPlayTourVideo.show()
    }

    val playVideo: (String) -> Unit = {

        val mPlayTourVideo = Dialog(this, android.R.style.Theme_Translucent)
        mPlayTourVideo.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        mPlayTourVideo.setContentView(R.layout.play_tour_video)
        mPlayTourVideo.setCancelable(true)

        mPlayTourVideo.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        val player: ExoPlayer
        if (nPlayer != null) {
            nPlayer!!.release()
            nPlayer = null
        }
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, this!!.getString(R.string.app_name)), defaultBandwidthMeter)
        dataSourceFac = dataSourceFactory
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(defaultBandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val contentMediaSource = buildMediaSource(Uri.parse(it))
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        player.setPlayWhenReady(true)
        mPlayTourVideo.pvTourVideo.player = player
        player.prepare(contentMediaSource)
        nPlayer = player

        nPlayer?.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                Log.d(TAG, "" + playbackParameters)
            }

            override fun onSeekProcessed() {
                Log.d(TAG, "")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                Log.d(TAG, "" + trackGroups)
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.d(TAG, "" + error!!.message)
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.d(TAG, "loading [$isLoading]")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                Log.d(TAG, "" + reason)
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.d(TAG, "" + repeatMode)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                Log.d(TAG, "" + shuffleModeEnabled)
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                Log.d(TAG, "" + timeline)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    if (nPlayer != null) {
                        nPlayer!!.release()
                        nPlayer = null
                        mPlayTourVideo.dismiss()
                    }
                }
            }
        })

        mPlayTourVideo.show()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        @C.ContentType val type = Util.inferContentType(uri)
        when (type) {
            /*C.TYPE_DASH:
               return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            C.TYPE_SS:
               return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);*/
            C.TYPE_HLS -> return HlsMediaSource.Factory(dataSourceFac).createMediaSource(uri)
            C.TYPE_OTHER -> return ExtractorMediaSource.Factory(dataSourceFac).createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type: $type") as Throwable
        }
    }

    private fun showSpotDetails() {

        val imgUrlList: ArrayList<String> = ArrayList()
        mRegionRegionSpot!!.forEachIndexed { index, e ->
            imgUrlList.add(mRegionRegionSpot!![index].sVideo!!)
        }

        DataHeb.addRequestUrl(imgUrlList)
        enqueueDownloads()

        rvSpotList!!.addItemDecoration(DividerItemDecoration(this@MapBoxActivity, LinearLayoutManager.HORIZONTAL))
        spotListAdapter = SpotListAdapter(applicationContext, this.mRegionRegionSpot!!, playVideo)
        spotListAdapter?.onItemClickedListener(this)
        val horizontalLayoutManager = LinearLayoutManager(this@MapBoxActivity, LinearLayoutManager.HORIZONTAL, false)
        rvSpotList!!.layoutManager = horizontalLayoutManager
        rvSpotList!!.adapter = spotListAdapter


        if (fetch != null)
            fetch!!.getDownloadsInGroup(mRegionPackageId.hashCode(), Func {
                val list = ArrayList<Download>(it)
                Collections.sort<Download>(list) { first, second -> java.lang.Long.compare(first.created, second.created) }
                for (download in list) {
                    spotListAdapter!!.addDownload(download)
                }

            }).addListener(fetchListener)

    }

    private val fetchListener = object : AbstractFetchListener() {
        override fun onAdded(download: Download) {
            spotListAdapter!!.addDownload(download)
        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
            spotListAdapter!!.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND)
        }

        override fun onCompleted(download: Download) {
            spotListAdapter!!.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND)
        }

        override fun onError(download: Download, error: Error, throwable: Throwable?) {
            super.onError(download, error, throwable)
            spotListAdapter!!.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND)
        }

        override fun onProgress(download: Download, etaInMilliseconds: Long, downloadedBytesPerSecond: Long) {
            spotListAdapter!!.update(download, etaInMilliseconds, downloadedBytesPerSecond)
        }

        override fun onPaused(download: Download) {
            spotListAdapter!!.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND)
        }

        override fun onResumed(download: Download) {
            spotListAdapter!!.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND)
        }

        override fun onCancelled(download: Download) {
            spotListAdapter!!.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND)
        }

        override fun onRemoved(download: Download) {
            spotListAdapter!!.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND)
        }

        override fun onDeleted(download: Download) {
            spotListAdapter!!.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND)
        }
    }


    companion object {
        private val TAG = "OffManActivity"
        // JSON encoding/decoding
        val JSON_CHARSET = "UTF-8"
        val JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME"
        val JSON_FIELD_REGION_ID = "FIELD_REGION_ID"

        val UNKNOWN_REMAINING_TIME: Long = -1
        val UNKNOWN_DOWNLOADED_BYTES_PER_SECOND: Long = 0
        val GROUP_ID = "listGroup".hashCode()
        val FETCH_NAMESPACE = "TOURGUIDE"
        var nPlayer: ExoPlayer? = null
    }


    //HANDLING ROOM DATABASE
    inner class InsertPackageSpots// only retain a weak reference to the activity
    internal constructor(val packageId: String, val myDataset: String, val context: Context) : AsyncTask<Void, Void, Boolean>() {

        // doInBackground methods runs on a worker thread
        override fun doInBackground(vararg objs: Void): Boolean? {

            var packageSpots = PackageSpots(packageId, myDataset)

            //adding to database
            DatabaseClient.getInstance(context)
                    .appDatabase
                    .tourPackageDao()
                    .insertPackageSpots(packageSpots)

            return true
        }

        // onPostExecute runs on main thread
        override fun onPostExecute(bool: Boolean?) {

        }
    }

    inner class UpdatePackageSpots// only retain a weak reference to the activity
    internal constructor(val packageId: String, val myDataset: String, val context: Context) : AsyncTask<Void, Void, Boolean>() {

        // doInBackground methods runs on a worker thread
        override fun doInBackground(vararg objs: Void): Boolean? {

            val packageSpots = PackageSpots(packageId, myDataset)

            /*var cat: CategoriesTable = CategoriesTable(myDataset.get(j).catName.toString(),
                    myDataset.get(j).catColor.toString()
                    , myDataset.get(j).catValue.toString(), myDataset.get(j).icon.toString(), false)*/


            //adding to database
            DatabaseClient.getInstance(context)
                    .appDatabase
                    .tourPackageDao()
                    .updatePackageSpots(packageId, packageSpotsListStr)

            return true
        }

        // onPostExecute runs on main thread
        override fun onPostExecute(bool: Boolean?) {
            if (bool!!) {
            }
        }
    }

    inner class RetrievePackageSpots// only retain a weak reference to the activity
    internal constructor(@SuppressLint("StaticFieldLeak") val context: Context, val packageSpotId: String, val internetStatus: Boolean) : AsyncTask<Void, Void, List<PackageSpots>>() {

        override fun doInBackground(vararg voids: Void): List<PackageSpots> {
            return DatabaseClient
                    .getInstance(context)
                    .appDatabase
                    .tourPackageDao()
                    .getPackageSpots(packageSpotId)

        }

        override fun onPostExecute(data: List<PackageSpots>) {
            super.onPostExecute(data)
            if (data.size > 0) {
                if (internetStatus) {
                    //fresh data available, udate the database
                    UpdatePackageSpots(mRegionToDownload?.packageId!!, packageSpotsListStr, baseContext).execute()
                } else {
                    //package exist in database
                    val gson = Gson()
                    val spots = gson.fromJson(data.get(0).packageSpots, PackageSpotsResponse::class.java)
                    mRegionRegionSpot = spots.data as List<DataItem>
                    mRegionToDownload = spots.mapData
                    mRegionRoute = spots.route
                    loadDownloadedRegion(spots.mapData?.packageId, spots.mapData?.regionName)
                }
            } else {
                if (packageSpotsListStr != "") {
                    InsertPackageSpots(mRegionToDownload?.packageId!!, packageSpotsListStr, baseContext).execute()
                } else {
                    showMessage(getString(R.string.txt_dwnld_tour_spots))
                }
            }

        }
    }
}