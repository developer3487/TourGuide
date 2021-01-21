package com.app.tourguide.ui.googleMap

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.app.tourguide.R
import com.app.tourguide.base_classes.BaseFragment
import com.app.tourguide.ui.MapAnimator
import com.app.tourguide.ui.avaliableplaces.pojomodel.Location
import com.app.tourguide.utils.Constants
import com.app.tourguide.utils.Util
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.content_map_type.*
import kotlinx.android.synthetic.main.fragment_google_map.*


/**
 */
class GoogleMapFragment : BaseFragment(), OnMapReadyCallback, com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {


    private var mapFragment: SupportMapFragment? = null
    private var locMarkers: Marker? = null
    private var mMap: GoogleMap? = null
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVALE = 5000
    private val FASTEST_INTERVALE = 5000
    private val DISPLACEMENT = 10
    private val PLAY_SERVICE_REQUEST_CODE = 7000
    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private val REQUEST_CHECK_SETTINGS = 214
    private val REQUEST_ENABLE_GPS = 516

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: android.location.Location? = null

    var routeMap: ArrayList<LatLng> = ArrayList()
    var locations: ArrayList<Location>? = null

    var latt: Double? = 0.0
    var langg: Double? = 0.0

    var currLatt: Double? = 0.0
    var currLangg: Double? = 0.0

    var currentMarker: Marker? = null

    private var polyLineList: ArrayList<LatLng>? = null
    private var index: Int = 0
    var next: Int = 0

    private var startPosition: LatLng? = null
    private var endPosition: LatLng? = null
    private var currentPosition: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpLocation()
        var builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
        builder.setAlwaysShow(true)
        mLocationSettingsRequest = builder.build()
        mSettingsClient = LocationServices.getSettingsClient(activity!!)
        mSettingsClient?.checkLocationSettings(mLocationSettingsRequest)
                ?.addOnSuccessListener {
                    //sucess perform task here
                }
                ?.addOnFailureListener {
                    var statusCode = (it as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                val rae = it as ResolvableApiException
                                rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                            } catch (sie: IntentSender.SendIntentException) {
                                Log.e("GPS", "Unable to execute request.")
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            Log.e("GPS", "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                        }

                    }
                }
                ?.addOnCanceledListener {
                    Log.e("GPS", "checkLocationSettings -> onCanceled");
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Success Perform Task Here
                }

                Activity.RESULT_CANCELED -> {
                    Log.e("GPS", "TourPackage denied to access location");
                    openGpsEnableSetting()
                }
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            val locationManager: LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!isGpsEnabled) {
                openGpsEnableSetting();
            } else {
                setUpLocation()
            }
        }
    }


    override fun onLocationChanged(lastLoc: android.location.Location?) {
        mLastLocation = lastLoc
        displayLocation()
    }

    private fun openGpsEnableSetting() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, REQUEST_ENABLE_GPS)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_google_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getSerializable(Constants.LOCATIONS)?.let {
            locations = arguments?.getSerializable(Constants.LOCATIONS) as ArrayList<Location>
        }

        mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment?.getMapAsync(this)
        MapsInitializer.initialize(activity)
        mapTypes.bringToFront()


        tv_normal.setOnClickListener {
            mMap?.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        }

        tv_satellite.setOnClickListener {
            mMap?.setMapType(GoogleMap.MAP_TYPE_SATELLITE)
        }

        tv_terrain.setOnClickListener {
            mMap?.setMapType(GoogleMap.MAP_TYPE_TERRAIN)
        }

    }

    fun getMap(): GoogleMap? {
        return this.mMap
    }


    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVALE.toLong()
        mLocationRequest!!.fastestInterval = FASTEST_INTERVALE.toLong()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.smallestDisplacement = DISPLACEMENT.toFloat()
    }

    private fun setUpLocation() {
        if (checkPlayServices()) {
            Log.d(TAG, "else setup")
            buildGoogleApiClient()
            createLocationRequest()
            displayLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)


        if (currentMarker != null) {
            currentMarker?.remove()
        }
        if (mLastLocation != null) {
            currentMarker = mMap?.addMarker(MarkerOptions().position(LatLng(mLastLocation?.latitude!!, mLastLocation?.longitude!!)).title("current"))
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mLastLocation?.latitude!!, mLastLocation?.longitude!!), 17.0f))
        }
    }

    private fun checkPlayServices(): Boolean {
        val resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICE_REQUEST_CODE).show()
            } else {
                Toast.makeText(activity, "This device is not supported", Toast.LENGTH_SHORT).show()
            }
            return false
        }
        return true
    }


    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this.activity!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient?.connect()
    }


    override fun onConnected(p0: Bundle?) {
        displayLocation()
        requestLocationUpdate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }


    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient?.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        getMap()?.getUiSettings()?.setRotateGesturesEnabled(true);
        googleMap?.getUiSettings()?.setTiltGesturesEnabled(false)
        googleMap?.getUiSettings()?.setCompassEnabled(false)
        googleMap?.getUiSettings()?.setMyLocationButtonEnabled(false)
        drawRoute()

        mMap?.setOnMapLoadedCallback(object : GoogleMap.OnMapLoadedCallback {
            override fun onMapLoaded() {

                val builder = LatLngBounds.Builder()

                routeMap.forEachIndexed { index, double ->

                    var icon: BitmapDescriptor? = null

                    val layoutInflater: LayoutInflater = LayoutInflater.from(activity)

                    val markerView: View = layoutInflater.inflate(R.layout.custom_marker_layout, null)

                    icon = BitmapDescriptorFactory.fromBitmap(Util.createDrawableFromView(activity, markerView, (index + 1).toString()));

                    builder.include(routeMap[index])

                    locMarkers = mMap?.addMarker(MarkerOptions().position(routeMap[index]).icon(icon))
                }
                val bounds = builder.build()
                // mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
                /*val zoom = mMap?.getCameraPosition()?.zoom
                mMap?.getUiSettings()?.setScrollGesturesEnabled(true)
                mMap?.moveCamera(CameraUpdateFactory.zoomTo(zoom!!.minus(1f)))
                if (routeMap.size > 1)
                    startAnim()*/

            }
        })

    }

    fun drawRoute() {
        routeMap.clear()
        locations?.forEachIndexed { index, double ->
            val loc: Location = locations?.get(index)!!
            latt = loc.locLatt!!.toString().toDouble()
            langg = loc.locLong!!.toString().toDouble()
            routeMap.add(LatLng(latt!!, langg!!))
            Log.e("Taggg", " " + routeMap[index].toString())
        }
    }

    fun startAnim() {
        if (mMap != null) {
            MapAnimator.getInstance().animateRoute(mMap, routeMap);
        } else {
            Toast.makeText(activity, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }


    override fun onResume() {
        super.onResume()
        mapFragment?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mapFragment != null)
                mapFragment?.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapFragment?.onLowMemory()
    }


    // future use
    private fun updateMarkerWithAnim() {
        val drawPathRunnabel = object : Runnable {
            override fun run() {
                if (index < polyLineList?.size!!.minus(1)) {
                    index++
                    next = index + 1
                }

                if (index < polyLineList?.size!!.minus(1)) {
                    startPosition = polyLineList?.get(index)
                    endPosition = polyLineList?.get(next)
                }
                val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                valueAnimator.duration = 3000
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.addUpdateListener { valueAnimator ->
                    var v: Float = valueAnimator.animatedFraction
                    currLangg = v * endPosition!!.longitude + (1 - v) * startPosition!!.longitude
                    currLatt = v * endPosition!!.latitude + (1 - v) * startPosition!!.latitude
                    val newPos = LatLng(currLatt!!, currLangg!!)
                    locMarkers!!.setPosition(newPos)
                    locMarkers!!.setAnchor(0.5f, 0.5f)
                    locMarkers!!.setRotation(getBearing(startPosition!!, newPos))
                    mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                    .target(newPos)
                                    .zoom(15.5f)
                                    .build()
                    ))
                }
                valueAnimator.start()
                // handler.postDelayed(this, 3000)
            }
        }
    }

    //Method for finding bearing between two points
    private fun getBearing(begin: LatLng, end: LatLng): Float {
        var lat: Double = Math.abs(begin.latitude - end.latitude);
        var lng: Double = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (Math.toDegrees(Math.atan(lng / lat)).toFloat());
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return ((90 - Math.toDegrees(Math.atan(lng / lat)).toFloat()) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (Math.toDegrees(Math.atan(lng / lat)).toFloat() + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return ((90 - Math.toDegrees(Math.atan(lng / lat)).toFloat()) + 270)
        return -1f
    }

}


