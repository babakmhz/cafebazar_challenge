package com.android.babakmhz.cafebazarchallenge.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.android.babakmhz.cafebazarchallenge.di.qualifier.ApplicationContext
import com.android.babakmhz.cafebazarchallenge.utils.FIRST_TIME_OPEN_KEY
import com.android.babakmhz.cafebazarchallenge.utils.PREFS_NAME
import javax.inject.Inject


class AppPrefs @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    fun setFirstTimeAppOpened(firstTime: Boolean) =
        prefs.edit().putBoolean(FIRST_TIME_OPEN_KEY, firstTime).apply()

    fun getFirstTimeAppOpened() = prefs.getBoolean(FIRST_TIME_OPEN_KEY, false)
}