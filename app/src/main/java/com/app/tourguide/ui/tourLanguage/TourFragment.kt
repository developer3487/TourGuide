package com.app.tourguide.ui.tourLanguage


import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tourguide.R
import com.app.tourguide.base_classes.BaseFragment
import com.app.tourguide.database.DatabaseClient
import com.app.tourguide.database.entity.PackageSpots
import com.app.tourguide.listeners.onItemClickedListener
import com.app.tourguide.ui.mapBox.MapBoxActivity
import com.app.tourguide.ui.placedetail.PlacesDetailsFragment
import com.app.tourguide.ui.tourLanguage.response.DataItem
import com.app.tourguide.utils.Constants
import com.app.tourguide.utils.security.ApiFailureTypes
import kotlinx.android.synthetic.main.fragment_tour.*
import java.util.*


class TourFragment : BaseFragment(), onItemClickedListener {


    private var mViewModel: TourViewModel? = null
    private var packageid: String = ""
    private var tourLangList: ArrayList<DataItem> = ArrayList()

    private var adapter: TourAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(TourViewModel::class.java)
        return inflater.inflate(R.layout.fragment_tour, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNetworkAvailable(view)) {
            getBundleArguments()
            attachObservers()
            rvTourLangList.layoutManager = LinearLayoutManager(activity)
            val divider = DividerItemDecoration(activity, DividerItemDecoration.HORIZONTAL)
            divider.setDrawable(ContextCompat.getDrawable(this.activity!!, R.drawable.divider)!!)
            rvTourLangList.addItemDecoration(divider)
        } else {
            rvTourLangList.visibility = View.INVISIBLE
            tv_lng_title.visibility = View.INVISIBLE
            RetrievePackageSpots(activity!!, arguments?.getString(Constants.PACKAGE_ID)!!, true).execute()
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
                val intent = Intent(activity, MapBoxActivity::class.java)
                intent.putExtra("packageId", arguments?.getString(Constants.PACKAGE_ID)!!)
                startActivity(intent)
            } else {
                showMessage(getString(R.string.no_internet_message))
            }

        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun attachObservers() {

        mViewModel?.response?.observe(this, Observer {
            it?.let {
                if (it.status == 1) {
                    tourLangList = it.data as ArrayList<DataItem>
                    adapter = TourAdapter(it.data, this.activity!!, this)
                    rvTourLangList.adapter = adapter
                }
                adapter?.notifyDataSetChanged()
            }
        })

        mViewModel?.resposneTourLang?.observe(this, Observer {
            it?.let {
                if (it.status == 1) {
                    val fragment = PlacesDetailsFragment()
                    val args = Bundle()
                    args.putString(Constants.PACKAGE_ID, packageid)
                    args.putBoolean(Constants.PREVIEW_STATUS, false)
                    fragment.arguments = args
                    addFragment(fragment, true, R.id.container_main)
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


    override fun onItemClickListener(pos: Int, uri: String) {
        if (arguments?.getBoolean(Constants.MAP_STATUS)!!) {
            val intent = Intent(activity, MapBoxActivity::class.java)
            startActivity(intent)
        } else {
            mViewModel!!.updateTourLanguage(packageid, deviceToken(), tourLangList.get(pos).languageId!!.toString())
        }
    }

    private fun getBundleArguments() {
        if (arguments != null) {
            packageid = arguments?.getString(Constants.PACKAGE_ID)!!
            hitApiToGetTourLanguages()
        }
    }


    private fun hitApiToGetTourLanguages() {
        if (!deviceToken().equals("") && !packageid.equals(""))
            mViewModel!!.getTourLanguages(packageid, deviceToken())
    }

}
