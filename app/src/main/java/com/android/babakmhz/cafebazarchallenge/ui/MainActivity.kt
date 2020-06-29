package com.android.babakmhz.cafebazarchallenge.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.babakmhz.cafebazarchallenge.R
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.databinding.MainActivityBinding
import com.android.babakmhz.cafebazarchallenge.ui.main.DetailsFragment
import com.android.babakmhz.cafebazarchallenge.ui.main.MainFragment
import com.android.babakmhz.cafebazarchallenge.utils.LOCATION_PERMISSION_CODE
import com.android.babakmhz.cafebazarchallenge.utils.LiveDataWrapper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(),com.google.android.gms.location.LocationListener {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var activityBinding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        activityBinding.viewModel = viewModel
        handleObservers()
        viewModel.setLoadingText(getString(R.string.loading_location))
        viewModel.init()
        activityBinding.executePendingBindings()
        handleLocationPermission()

        if (savedInstanceState == null) {
            viewModel.setCurrentFragment(MainFragment.newInstance())
        }


    }

    private fun handleObservers() {
        viewModel.currentFragment.observe(this, fragmentsObserver)
        viewModel.locationPermission.observe(this, requestPermissionObserver)
        viewModel.locations.observe(this, locationsObserver)
    }

    private val fragmentsObserver = Observer<Fragment> {
        if (it != null && it !is DetailsFragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, it)
                .commitNow()
        } else if (it is DetailsFragment) {
            DetailsFragment.newInstance().show(supportFragmentManager, "")
        }
    }

    private fun handleLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).toTypedArray(), LOCATION_PERMISSION_CODE
            )

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_PERMISSION_CODE)
            if (resultCode == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            } else {
                viewModel.setLocationPermission(false)
                viewModel.setLoadingText(getString(R.string.accept_permission))
            }

    }

    private val requestPermissionObserver = Observer<Boolean> {
        if (it)
            handleLocationPermission()
    }

    private val locationsObserver = Observer<LiveDataWrapper<List<LocationModel>>> {
        if (it.responseRESPONSESTATUS == LiveDataWrapper.RESPONSESTATUS.LOCATION_SHOULD_LOAD_REMOTELY) {
            // TODO : get data from gps
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                viewModel.handleLocationUpdates(location)
            }

    }

    override fun onLocationChanged(p0: Location?) {
        TODO("Not yet implemented")
    }

//    private fun requestLocationUpdates() {
//        val locationManager = getSystemService(Context.LOCATION_SERVICE)
//    }

}


