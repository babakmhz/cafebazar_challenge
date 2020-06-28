package com.android.babakmhz.cafebazarchallenge.ui.main

import android.location.Location
import com.android.babakmhz.cafebazarchallenge.data.db.AppDatabase
import com.android.babakmhz.cafebazarchallenge.data.db.ConvertFormattedAddressToString
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.data.network.ApiResponse
import com.android.babakmhz.cafebazarchallenge.data.network.ApiService
import com.android.babakmhz.cafebazarchallenge.data.prefs.AppPrefs
import com.android.babakmhz.cafebazarchallenge.utils.*
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainUseCase @Inject constructor(
    private val db: AppDatabase, private val prefs: AppPrefs,
    private val apiService: ApiService
) {

    suspend fun saveLocations(locations: List<LocationModel>) {
        db.locationsDao().clearLocationTable()
        db.locationsDao().insertLocations(locations)
    }

    suspend fun getLocationFromLocalSource(): List<LocationModel> {
        return db.locationsDao().getAllLocations()
    }

    suspend fun getLocationFromRemoteSource(location: Location): List<LocationModel>? {

        val apiResult = apiService.getLocation(
            CLIENT_ID,
            CLIENT_SECRET,
            V,
            "${location.latitude},${location.longitude}",
            LIMIT
        )

        if (apiResult.meta.code != 200) {
            return null
        }

        return resolveApiObjectToDbModel(apiResult)

    }

    private fun resolveApiObjectToDbModel(apiResponse: ApiResponse): List<LocationModel> {
        val items = apiResponse.response.groups.items
        val locations = arrayListOf<LocationModel>()
        for (item in items) {
            val venue = item.reasons.venue
            val icon = venue.location.categories[0].icon
            val iconUrl = "${icon.prefix}${icon.suffix}"
            val _location = com.android.babakmhz.cafebazarchallenge.data.db.Location(
                venue.id,
                venue.location.address,
                venue.location.lat,
                venue.location.lng,
                venue.location.distance,
                venue.location.city,
                venue.location.country,
                iconUrl,
                ConvertFormattedAddressToString.convert(venue.location.formattedAddress)
            )
            locations.add(LocationModel(venue.id, venue.name, _location))
        }
        return locations
    }

    suspend fun shouldRequestLocationUpdates(lastKnownLocation: Location?): Boolean {
        if (lastKnownLocation == null) return true

        if (prefs.getLastKnownLocation().equals("") || prefs.getNotFirstTimeAppOpened()) return true

        if (db.locationsDao().getAllLocations().isEmpty()) return true

        try {
            val oldLocation = AppUtils.getLatLngFromString(prefs.getLastKnownLocation())
            val currentLocation = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
            if (AppUtils.measureDistanceBetweenTwoLocations(
                    oldLocation!!,
                    currentLocation
                )!! >= RADIUS_LIMIT
            )
                return true
        } catch (ex: Exception) {
            AppLogger.e(ex, "Exception Happened")
            return true
        }

        return false
    }

}