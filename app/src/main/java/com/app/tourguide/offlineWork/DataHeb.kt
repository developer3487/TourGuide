package com.app.tourguide.offlineWork

import android.net.Uri
import android.os.Environment
import com.tonyodev.fetch2.Request
import java.util.*


class DataHeb {
    companion object {

        private var fetchRequests: List<Request>? = null

        fun addRequestUrl(imgUrlList: ArrayList<String>): List<Request>{
            val requests = ArrayList<Request>()
            for (sampleUrl in imgUrlList) {
                val request = Request(sampleUrl, getFilePath(sampleUrl))
                requests.add(request)
            }
            fetchRequests = requests
            return requests
        }

        val saveDir: String
            get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/fetch"

        fun getFetchRequestWithGroupId(groupId: Int): List<Request> {
            val requests = fetchRequests
            for (request in requests!!) {
                request.groupId = groupId
            }
            return requests
        }

        private fun getFilePath(url: String): String {
            val uri = Uri.parse(url)
            //boolean isContain = containsURL(uri.getLastPathSegment());
            val fileName = uri.lastPathSegment
            //if (fileName.contains("\""))
            //fileName = fileName.replaceAll("\\\\", "");
            val dir = saveDir
            return "$dir/DownloadList/$fileName"
        }

    }

}
