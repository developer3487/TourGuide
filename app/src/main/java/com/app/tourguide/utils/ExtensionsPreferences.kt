package com.app.tourguide.utils

import com.hmu.kotlin.utils.security.EncryptedPreferences


/**
 * Generic Extension function to store values inside the SharedPreferences
 */
fun <T> EncryptedPreferences.saveValue(key: String, value: T) {
    val editor = this.edit()
    when (value) {
        is String -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Long -> edit { it.putLong(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Boolean -> edit { it.putBoolean(key, value)  }


    }
    editor.commit()
}

fun EncryptedPreferences.clearValue(key: String) {
    val editor = this.edit()
    editor.putString(key, "")
    editor.commit()
}

/**
 * Generic Extension function to get values from the SharedPreferences
 */
inline fun <reified T : Any> EncryptedPreferences.getValue(key: String, defaultValue: T?): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> defaultValue
    }
}

/*High order Inline function to store values inside the shared preferences*/
inline fun EncryptedPreferences.edit(operation: (EncryptedPreferences.EncryptedEditor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}
