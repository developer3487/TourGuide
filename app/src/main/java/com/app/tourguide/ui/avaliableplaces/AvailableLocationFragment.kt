package com.app.tourguide.ui.avaliableplaces

import Preferences
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tourguide.R
import com.app.tourguide.base_classes.BaseFragment
import com.app.tourguide.database.DatabaseClient
import com.app.tourguide.database.entity.TourPackage
import com.app.tourguide.expandlelist.ExpandableListViewAdapter
import com.app.tourguide.ui.avaliableplaces.model.DataItem
import com.app.tourguide.ui.avaliableplaces.model.ResponseAvailableTour
import com.app.tourguide.ui.tourLanguage.TourFragment
import com.app.tourguide.utils.Constants
import com.app.tourguide.utils.saveValue
import com.app.tourguide.utils.security.ApiFailureTypes
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_availablelocation.*


class AvailableLocationFragment : BaseFragment() {

    private var expandableListViewAdapter: ExpandableListViewAdapter? = null
    private lateinit var conext: Context

    private var placesList: ArrayList<DataItem> = ArrayList()
    private var mViewModel: AvaliablePlacesModel? = null
    private val prevExpandPosition = intArrayOf(-1)
    private val gson = Gson()
    private var placeListStr: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        conext = (activity as Context?)!!
        mViewModel = ViewModelProviders.of(this).get(AvaliablePlacesModel::class.java)
        return inflater.inflate(R.layout.fragment_availablelocation, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

        mViewModel?.postDeviceToken(deviceToken(),getMacAddress())


    }


    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable(view)) {
            attachObserversToGetPlacesList()
        } else {
            RetrieveAllTourPackage(activity!!).execute()
        }
    }

    private fun initListeners() {
        expandableListViewAdapter = ExpandableListViewAdapter(conext, placesList)
        rv_availableplaces.setAdapter(expandableListViewAdapter)

        rv_availableplaces!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->

            val fragment = TourFragment()
            val args = Bundle()
            args.putString(Constants.PACKAGE_ID, placesList[groupPosition].tourPackages!![childPosition]!!.id)
            args.putBoolean(Constants.MAP_STATUS, false)
            fragment.arguments = args
            addFragment(fragment, true, R.id.container_main)
            false

        }


        rv_availableplaces.setOnGroupExpandListener { groupPosition ->
            if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] !== groupPosition) {
                rv_availableplaces.collapseGroup(prevExpandPosition[0])
            }
            prevExpandPosition[0] = groupPosition
        }


        /*   // ExpandableListView Group expanded listener
           rv_availableplaces.setOnGroupExpandListener { groupPosition -> }*/

        // ExpandableListView Group collapsed listener
        rv_availableplaces.setOnGroupCollapseListener { groupPosition ->
        }
    }


    private fun attachObserversToGetPlacesList() {
        mViewModel?.responsePlaces?.observe(this, Observer {
            it?.let {
                if (it.status == 1) {
                    placesList = it.data as ArrayList<DataItem>

                    placeListStr = gson.toJson(it)

                    expandableListViewAdapter = ExpandableListViewAdapter(conext, placesList)
                    rv_availableplaces.setAdapter(expandableListViewAdapter)
                    expandableListViewAdapter?.notifyDataSetChanged()

                    RetrieveAllTourPackage(activity!!).execute()
                }
            }
        })

        mViewModel?.postDeviceTokenResp?.observe(this, Observer
        {
            it?.let {
                if (it.status == 1) {
                    Preferences.prefs?.saveValue(Constants.IS_DEVICE_ID_SEND, true)
                    mViewModel?.getPackagesData(deviceToken())
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

        mViewModel?.onFailure?.observe(this, Observer {
            it?.let {
                showSnackBar(ApiFailureTypes().getFailureMessage(it))
            }
        })

    }

    inner class InsertAllTourPackage// only retain a weak reference to the activity
    internal constructor(val myDataset: String, val context: Context) : AsyncTask<Void, Void, Boolean>() {

        // doInBackground methods runs on a worker thread
        override fun doInBackground(vararg objs: Void): Boolean? {

            var tourPackage = TourPackage("1", myDataset)
            /*var cat: CategoriesTable = CategoriesTable(myDataset.get(j).catName.toString(),
                    myDataset.get(j).catColor.toString()
                    , myDataset.get(j).catValue.toString(), myDataset.get(j).icon.toString(), false)*/


            //adding to database
            DatabaseClient.getInstance(context).appDatabase
                    .tourPackageDao()
                    .insert(tourPackage)

            return true
        }

        // onPostExecute runs on main thread
        override fun onPostExecute(bool: Boolean?) {
            if (bool!!) {

            }
        }

    }


    inner class UpdateAllTourPackage// only retain a weak reference to the activity
    internal constructor(val myDataset: String, val context: Context) : AsyncTask<Void, Void, Boolean>() {

        // doInBackground methods runs on a worker thread
        override fun doInBackground(vararg objs: Void): Boolean? {

            var tourPackage = TourPackage("1", myDataset)
            /*var cat: CategoriesTable = CategoriesTable(myDataset.get(j).catName.toString(),
                    myDataset.get(j).catColor.toString()
                    , myDataset.get(j).catValue.toString(), myDataset.get(j).icon.toString(), false)*/


            //adding to database
            DatabaseClient.getInstance(context).appDatabase
                    .tourPackageDao()
                    .update("1", placeListStr)

            return true
        }

        // onPostExecute runs on main thread
        override fun onPostExecute(bool: Boolean?) {
            if (bool!!) {
            }
        }
    }

    inner class RetrieveAllTourPackage// only retain a weak reference to the activity
    internal constructor(@SuppressLint("StaticFieldLeak") val context: Context) : AsyncTask<Void, Void, List<TourPackage>>() {

        override fun doInBackground(vararg voids: Void): List<TourPackage> {
            return DatabaseClient
                    .getInstance(context)
                    .appDatabase
                    .tourPackageDao()
                    .getAll()
        }

        override fun onPostExecute(tasks: List<TourPackage>?) {
            super.onPostExecute(tasks)

            /*tasks?.get(0)?.spots
            var data:DataItem=tasks?.get(0)?.spots
            tasks?.get(0)?.spots*/

            if (tasks!!.size != 0) {
                //run update
                if (placeListStr != "") {
                    UpdateAllTourPackage(placeListStr, activity!!)
                }

                val gson = Gson()
                val data = gson.fromJson(tasks.get(0).spots, ResponseAvailableTour::class.java)

                placesList = data.data as ArrayList<DataItem>

                expandableListViewAdapter = ExpandableListViewAdapter(context, placesList)
                rv_availableplaces.setAdapter(expandableListViewAdapter)
                expandableListViewAdapter?.notifyDataSetChanged()
            } else {
                //run insert
                if (placeListStr != "") {
                    InsertAllTourPackage(placeListStr, activity!!).execute()
                }
            }
        }
    }

}

