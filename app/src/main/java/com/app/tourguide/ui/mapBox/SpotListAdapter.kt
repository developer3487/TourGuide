package com.app.tourguide.ui.mapBox

import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tourguide.R
import com.app.tourguide.listeners.onItemClickedListener
import com.app.tourguide.ui.mapBox.response.DataItem
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status
import kotlinx.android.synthetic.main.dialog_spot_detail.view.*
import java.io.File
import java.util.*


class SpotListAdapter(internal var context: Context, internal var mRegionRegionSpot: List<DataItem>, val playVideo: (String) -> Unit) : RecyclerView.Adapter<SpotListAdapter.SpotListViewHolder>() {


    companion object {
        val downloads = ArrayList<SpotListAdapter.DownloadData>()

    }

    private lateinit var listener: onItemClickedListener

    fun onItemClickedListener(listener: onItemClickedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotListViewHolder {
        //inflate the layout file
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_spot_detail, parent, false) as ConstraintLayout
        return SpotListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpotListViewHolder, position: Int) {

        try {

            holder.layout.tvSpotTitle.text = mRegionRegionSpot[position].spotName

            //holder.layout.tvSpotDetail.text = mRegionRegionSpot[position].spotDesc

            Picasso.get()
                    .load(mRegionRegionSpot[position].vThumbnail)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.layout.ivSpotImage, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            Picasso.get()
                                    .load(mRegionRegionSpot[position].vThumbnail)
                                    .into(holder.layout.ivSpotImage, object : Callback {
                                        override fun onSuccess() {

                                        }

                                        override fun onError(e: Exception?) {
                                            print("Couldn't fetch data")
                                        }
                                    })
                        }
                    })


            val downloadData = downloads[position]
            //val (language, icon) = availLangs[downloadData.pos]

            var url = ""
            if (downloadData.download != null) {
                url = downloadData.download!!.url
            }
            val uri = Uri.parse(url)
            val status = downloadData.download!!.status


            var progress = downloadData.download!!.progress
            if (progress == -1) { // Download progress is undermined at the moment.
                progress = 0
            }

            holder.layout.pbVideoBar.progress = progress

            holder.layout.ivSpotImage.setOnClickListener {
                val file = File(downloadData.download!!.file)
                var uri1: Uri? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    uri1 = Uri.parse(file.path)
                } else {
                    uri1 = Uri.fromFile(file)
                }
                //playVideo(uri1.toString())
                listener.onItemClickListener(position, uri1.toString())
            }

            when (status) {
                Status.COMPLETED -> {
                    holder.layout.pbVideoBar.visibility = View.GONE
                }

                Status.FAILED -> {

                }
                Status.PAUSED -> {

                }
                Status.DOWNLOADING, Status.QUEUED -> {
                }
                Status.ADDED -> {
                }
                else -> {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    override fun getItemCount(): Int {
        return downloads.size
    }

    class SpotListViewHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout)


    fun addDownload(download: Download) {
        var found = false
        var data: SpotListAdapter.DownloadData? = null
        var dataPosition = -1
        for (i in downloads.indices) {
            val downloadData = downloads[i]
            if (downloadData.id == download.id) {
                data = downloadData
                dataPosition = i
                downloadData.pos = dataPosition
                found = true
                break
            }
        }
        if (!found) {
            val downloadData = SpotListAdapter.DownloadData()
            downloadData.id = download.id
            downloadData.download = download
            downloads.add(downloadData)
            for (i in downloads.indices) {
                val downloadDataN = downloads[i]
                downloadDataN.pos = i
            }
            notifyItemInserted(downloads.size - 1)
        } else {
            data!!.download = download
            notifyItemChanged(dataPosition)
        }
    }

    fun update(download: Download, eta: Long, downloadedBytesPerSecond: Long) {
        for (position in downloads.indices) {
            val downloadData = downloads[position]
            downloadData.pos = position
            if (downloadData.id == download.id) {
                when (download.status) {
                    Status.REMOVED, Status.DELETED -> {
                        downloads.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    else -> {
                        downloadData.download = download
                        downloadData.eta = eta
                        downloadData.downloadedBytesPerSecond = downloadedBytesPerSecond
                        notifyItemChanged(position)
                    }
                }
                return
            }
        }
    }

    private fun getStatusString(status: Status): String {
        when (status) {
            Status.COMPLETED -> return "Done"
            Status.DOWNLOADING -> return "Downloading"
            Status.FAILED -> return "Error"
            Status.PAUSED -> return "Paused"
            Status.QUEUED -> return "Waiting in Queue"
            Status.REMOVED -> return "Removed"
            Status.NONE -> return "Not Queued"
            else -> return "Unknown"
        }
    }

    class DownloadData {
        var id: Int = 0
        var download: Download? = null
        var pos: Int = 0
        internal var eta: Long = -1
        internal var downloadedBytesPerSecond: Long = 0

        override fun hashCode(): Int {
            return id
        }

        override fun toString(): String {
            return if (download == null) {
                ""
            } else download!!.toString()
        }

        override fun equals(obj: Any?): Boolean {
            return obj === this || obj is DownloadData && obj.id == id
        }
    }
}