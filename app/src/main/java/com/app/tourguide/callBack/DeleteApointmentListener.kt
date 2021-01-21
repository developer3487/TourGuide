package com.hmu.kotlin.callBack

/**
 * Created by android on 22/3/18.
 */
interface DeleteApointmentListener {
    fun selectedItem(position: Int, id: Int?, place:String)
    fun todoSelected(position: Int)


}