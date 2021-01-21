package com.app.tourguide.ui.tourLanguage

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tourguide.R
import com.app.tourguide.listeners.onItemClickedListener
import com.app.tourguide.ui.tourLanguage.response.DataItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_tour.view.*
import java.util.ArrayList

class TourAdapter(private var list: ArrayList<DataItem>, private var context: Context,
                  private var onItemClicked: onItemClickedListener) : RecyclerView.Adapter<TourAdapter.MyViewHolder>() {

    class MyViewHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout)


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_tour, parent, false) as ConstraintLayout
        return MyViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(list[position].icon).into(holder.layout.ivLangFlag)
        holder.layout.ivLangName.text = list[position].language

        if (list.size - 1 == position) {
            holder.layout.divider.visibility = View.GONE
        }

        holder.layout.clLangRoot.setOnClickListener {
            onItemClicked.onItemClickListener(position, "")
        }


    }

}