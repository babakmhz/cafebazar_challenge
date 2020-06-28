package com.android.babakmhz.cafebazarchallenge.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.android.babakmhz.cafebazarchallenge.di.qualifier.ApplicationContext
import com.android.babakmhz.cafebazarchallenge.utils.FIRST_TIME_OPEN_KEY
import com.android.babakmhz.cafebazarchallenge.utils.LAST_LOCATION_KEY
import com.android.babakmhz.cafebazarchallenge.utils.PREFS_NAME
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppPrefs @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    fun setNotFirstTimeAppOpened(firstTime: Boolean) =
        prefs.edit().putBoolean(FIRST_TIME_OPEN_KEY, firstTime).apply()

    fun getNotFirstTimeAppOpened() = prefs.getBoolean(FIRST_TIME_OPEN_KEY, false)


    fun setLastKnownLocation(location: String) =
        prefs.edit().putString(LAST_LOCATION_KEY, location).apply()


    fun getLastKnownLocation() = prefs.getString(LAST_LOCATION_KEY, "")
}