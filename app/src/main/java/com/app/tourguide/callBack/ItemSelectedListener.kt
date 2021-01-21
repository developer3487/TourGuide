package com.app.tourguide.callBack

import android.net.Uri
import org.jetbrains.annotations.NotNull

/**
 * Created by shivam on 30/3/18.
 */
interface ItemSelectedListener {

    fun selectedItem(pos: Int, type: @NotNull String, url: Uri)


}
