package com.android.babakmhz.cafebazarchallenge.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.babakmhz.cafebazarchallenge.utils.DB_VERSION


@Database(entities = [LocationModel::class], version = DB_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationsDao(): LocationsDao

}