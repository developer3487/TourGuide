package com.app.tourguide.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import android.view.KeyEvent.KEYCODE_DEL
import android.view.View


class MultiTextWatcher {

    private var callback: TextWatcherWithInstance? = null

    fun setCallback(callback: TextWatcherWithInstance): MultiTextWatcher {
        this.callback = callback
        return this
    }


    fun registerEditText(editText: EditText): MultiTextWatcher {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                callback!!.beforeTextChanged(editText, s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                callback!!.onTextChanged(editText, s, start, before, count)
            }

            override fun afterTextChanged(editable: Editable) {
                callback!!.afterTextChanged(editText, editable)
            }
        })

        return this
    }

     interface TextWatcherWithInstance {
        fun beforeTextChanged(editText: EditText, s: CharSequence, start: Int, count: Int, after: Int)

        fun onTextChanged(editText: EditText, s: CharSequence, start: Int, before: Int, count: Int)

        fun afterTextChanged(editText: EditText, editable: Editable)
    }
}