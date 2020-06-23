package com.android.babakmhz.cafebazarchallenge.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Locations")
data class LocationModel(
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
    @Embedded val location: Location
)

@Entity(tableName = "Place")
data class Location(
    @PrimaryKey val locationId: Long,
    @ColumnInfo val address: String,
    @ColumnInfo val lat: String,
    @ColumnInfo val lng: String,
    @ColumnInfo val distance: Int,
    @ColumnInfo val city: String,
    @ColumnInfo val country: String,
    @ColumnInfo val icon: String
)