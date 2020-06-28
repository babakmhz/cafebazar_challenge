package com.android.babakmhz.cafebazarchallenge.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

object AppUtils {

    fun measureDistanceBetweenTwoLocations(
        latLng1: LatLng?,
        latLng2: LatLng?
    ): Int? {
        if (latLng1 == null || latLng2 == null)
            return null
        return SphericalUtil.computeDistanceBetween(latLng1, latLng2).toInt()
    }

    fun getLatLngFromString(latLng: String?): LatLng? {
        // lat,lng
        return try {
            val lat = latLng?.substring(0, latLng.lastIndexOf(","))
            val lng = latLng?.substring(latLng.lastIndexOf(",") + 1)
            LatLng(lat!!.toDouble(), lng!!.toDouble())
        } catch (ex: Exception) {
            AppLogger.e(ex, "Exception Happened")
            null
        }
    }

}

