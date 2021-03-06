package com.android.babakmhz.cafebazarchallenge.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.babakmhz.cafebazarchallenge.utils.DB_VERSION
import javax.inject.Inject
import javax.inject.Singleton

@Database(entities = [LocationModel::class], version = DB_VERSION,exportSchema = false)
abstract class AppDatabase  : RoomDatabase() {
    abstract fun locationsDao(): LocationsDao

}