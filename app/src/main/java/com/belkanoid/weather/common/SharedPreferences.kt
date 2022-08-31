package com.belkanoid.weather.common

import android.content.Context
import android.preference.PreferenceManager
import com.belkanoid.weather.common.Constants.QUERY

object SharedPreferences {

    fun getCity(context: Context): String? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(QUERY, null)
    }

    fun setCity(context: Context, city: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(QUERY, city)
            .apply()
    }
    fun discardCity(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .clear()
            .apply()
    }
}