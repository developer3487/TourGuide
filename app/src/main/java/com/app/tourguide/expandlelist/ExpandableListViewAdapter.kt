package com.app.tourguide.expandlelist

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import android.widget.Toast

import com.app.tourguide.R

import com.app.tourguide.ui.avaliableplaces.model.DataItem
import com.app.tourguide.ui.avaliableplaces.model.TourPackagesItem

class ExpandableListViewAdapter(private val context: Context, private val listDataGroup: ArrayList<DataItem>) : BaseExpandableListAdapter() {


    override fun getChild(groupPosition: Int, childPosititon: Int): TourPackagesItem? {
        //return this.listDataGroup[groupPosition].tourPackages!![childPosititon]
        return this.listDataGroup[groupPosition].tourPackages?.get(childPosititon)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val childText = listDataGroup[groupPosition].tourPackages!!.get(childPosition)?.pName

        if (convertView == null) {
            val layoutInflater = this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_row_child, null)
        }

        val textViewChild = convertView!!.findViewById<TextView>(R.id.tv_plce_name)
        textViewChild.text = childText
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {

        if (this.listDataGroup[groupPosition].tourPackages == null) {
            Toast.makeText(context, "No Tour Package  Available", Toast.LENGTH_LONG).show()
            return 0
        }
        return this.listDataGroup[groupPosition].tourPackages!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return this.listDataGroup[groupPosition]
    }

    override fun getGroupCount(): Int {
        return this.listDataGroup.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val headerTitle = listDataGroup.get(groupPosition).location?.name
            if (convertView == null) {
            val layoutInflater = this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_row_group, null)
        }

        val textViewGroup = convertView!!.findViewById<TextView>(R.id.textViewGroup)
        textViewGroup.setTypeface(null, Typeface.BOLD)
        textViewGroup.text = headerTitle

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}