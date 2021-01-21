package com.app.tourguide.utils

import android.util.Patterns
import android.widget.EditText

object Validations {


    fun isEmpty(et: EditText): Boolean {
        val data = et.text.toString().trim { it <= ' ' }
        if (data.isEmpty()) {
            et.error = "This field is required"
            return false
        }
        return true
    }


    fun isValidEmail(et: EditText): Boolean {
        if (Patterns.EMAIL_ADDRESS.matcher(et.text.toString()).matches()) {
            return true
        }
        et.error = "Enter a valid email address"
        et.requestFocus()
        return false
    }

    fun isValidPassword(et: EditText): Boolean {
        val password = et.text.toString().trim { it <= ' ' }
        if (password.length > 5) {
            return true
        }
        et.error = "Password must be of 6-8 characters"
        et.requestFocus()
        return false
    }


    fun isValidNumber(et: EditText): Boolean {
        val number = et.text.toString().trim { it <= ' ' }
        try {
            if (!number.matches("[0]*[1-9][0-9]*".toRegex())) {
                et.error = "Enter a valid number"
                et.requestFocus()
                return false
            } else if (number.length != 10) {
                et.error = "Enter a 10 digit number"
                et.requestFocus()
                return false
            }
            return true
        } catch (ex: NumberFormatException) {
            et.error = "Not a valid number"
            et.requestFocus()
            return false
        }

    }


}
