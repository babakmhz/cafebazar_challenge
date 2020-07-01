package com.android.babakmhz.cafebazarchallenge.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationsDao {

    @Query("SELECT * from Locations order by location_distance ASC")
    suspend fun getAllLocations(): List<LocationModel>

    @Query("DELETE FROM Locations")
    suspend fun clearLocationTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationModel>)


}