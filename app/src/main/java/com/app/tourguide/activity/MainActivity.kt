package com.app.tourguide.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.Gravity
import android.view.View
import com.app.tourguide.R
import com.app.tourguide.base_classes.BaseActivity
import com.app.tourguide.ui.avaliableplaces.AvailableLocationFragment
import com.app.tourguide.ui.language.ChooseLanguageFragment
import com.app.tourguide.ui.placedetail.PlacesDetailsFragment
import com.app.tourguide.ui.videoView.VideoViewFragment
import com.app.tourguide.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_view.*


class MainActivity : BaseActivity(), FragmentManager.OnBackStackChangedListener {


    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var fragment: Fragment


    override fun onBackStackChanged() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container_main)
        if (fragment is VideoViewFragment) {
            iv_toggle_menu.visibility = View.INVISIBLE
        } else {
            iv_toggle_menu.visibility = View.VISIBLE
        }
    }

    override fun getID(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.addOnBackStackChangedListener(this)
        /*supportFragmentManager.addOnBackStackChangedListener {
            FragmentManager.OnBackStackChangedListener {
                var fragment = supportFragmentManager.findFragmentById(R.id.container_main)
                Log.i("BackStack", "back stack changed " + fragment)
            }
        }*/

    }

    override fun iniView(savedInstanceState: Bundle?) {
        initViews()
    }


    private fun initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        navigationView = this.findViewById(R.id.design_navigation_view)

        tv_language.setOnClickListener {
            fragment = supportFragmentManager?.findFragmentById(R.id.container_main)!!
            val fragmentChoose = ChooseLanguageFragment()
            val args = Bundle()
            args.putBoolean(Constants.PREVIEW_STATUS, false)
            fragmentChoose.arguments = args
            if (fragment != null && fragment is ChooseLanguageFragment) {
                var chooseLang = fragment as ChooseLanguageFragment
                chooseLang.updateView()
            } else {
                addFragment(fragmentChoose, true, R.id.container_main)
            }
            mDrawerLayout.closeDrawer(GravityCompat.START)
        }
        tv_tour_list.setOnClickListener {
            fragment = supportFragmentManager?.findFragmentById(R.id.container_main)!!

            if (fragment != null && (fragment is ChooseLanguageFragment || fragment is PlacesDetailsFragment)) {
                supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            mDrawerLayout.closeDrawer(GravityCompat.START)
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
                iv_toggle_menu.visibility = View.GONE
            }
        })

        iv_toggle_menu.setOnClickListener {
            if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START)
                iv_toggle_menu.visibility = View.VISIBLE
            } else {
                iv_toggle_menu.visibility = View.GONE
                mDrawerLayout.openDrawer(GravityCompat.START)
            }
        }

        iv_closedrawer.setOnClickListener {
            mDrawerLayout.closeDrawer(GravityCompat.START)
            iv_toggle_menu.visibility = View.VISIBLE
        }

        iv_toggle_menu.visibility = View.VISIBLE
        replaceFragment(AvailableLocationFragment(), R.id.container_main)

    }


}



