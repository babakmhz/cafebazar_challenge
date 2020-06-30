package com.android.babakmhz.cafebazarchallenge.ui.main

import android.location.Location
import com.android.babakmhz.cafebazarchallenge.data.db.ConvertFormattedAddressToString
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.data.db.LocationsDao
import com.android.babakmhz.cafebazarchallenge.data.network.ApiResponse
import com.android.babakmhz.cafebazarchallenge.data.network.ApiService
import com.android.babakmhz.cafebazarchallenge.data.prefs.AppPrefs
import com.android.babakmhz.cafebazarchallenge.utils.*
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainUseCase @Inject constructor(
    private val db: LocationsDao, private val prefs: AppPrefs,
    private val apiService: ApiService
) {

    suspend fun saveLocations(locations: List<LocationModel>) {
        db.clearLocationTable()
        db.insertLocations(locations)
    }

    suspend fun getLocationFromLocalSource(): List<LocationModel> {
        return db.getAllLocations()
    }

    suspend fun getLocationFromRemoteSource(location: Location?): List<LocationModel>? {

        if (location == null) return null

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
        val items = apiResponse.response.groups[0].items
        val locations = arrayListOf<LocationModel>()
        for (item in items) {
            try {

                val venue = item.venue
                val icon = venue.categories[0].icon
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
                    ConvertFormattedAddressToString.convert(venue.location.formattedAddress),
                    venue.categories[0].name
                )
                locations.add(LocationModel(venue.id, venue.name, _location))
            } catch (ex: Exception) {
                AppLogger.i("EXCEPTION HAPPENED IN PARSING RESULT : \n ${ex.toString()}")
                AppLogger.i("Locations In List SIZE ${locations.size}")
            }
        }
        return locations
    }

    suspend fun shouldRequestLocationUpdates(lastKnownLocation: Location?): Boolean {
        if (lastKnownLocation == null) return true

        if (prefs.getLastKnownLocation().equals("") || prefs.getNotFirstTimeAppOpened()) return true

        if (db.getAllLocations().isEmpty()) return true

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
            AppLogger.e(ex, "Exception Happened IN LOCATION SHOULD UPDATE")
            return true
        }

        return false
    }

}