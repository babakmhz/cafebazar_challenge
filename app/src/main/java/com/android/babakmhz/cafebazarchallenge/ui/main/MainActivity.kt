package com.android.babakmhz.cafebazarchallenge.ui.main
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.android.babakmhz.cafebazarchallenge.R
import com.android.babakmhz.cafebazarchallenge.data.db.AppDatabase
import com.android.babakmhz.cafebazarchallenge.data.db.LocationsDao
import com.android.babakmhz.cafebazarchallenge.ui.main.MainFragment
import com.android.babakmhz.cafebazarchallenge.utils.DB_NAME

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}