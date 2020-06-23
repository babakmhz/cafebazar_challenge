package com.android.babakmhz.cafebazarchallenge.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationsDao {

    @Query("SELECT * from Locations")
    suspend fun getAllLocations(): List<LocationModel>

    @Insert
    suspend fun insertLocations(vararg locations: LocationModel)


}