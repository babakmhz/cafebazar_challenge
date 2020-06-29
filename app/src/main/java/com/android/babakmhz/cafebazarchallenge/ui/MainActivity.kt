package com.android.babakmhz.cafebazarchallenge.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import com.android.babakmhz.cafebazarchallenge.utils.AppLogger
import com.android.babakmhz.cafebazarchallenge.utils.LOCATION_PERMISSION_CODE
import com.android.babakmhz.cafebazarchallenge.utils.LiveDataWrapper
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

//as i noticed java programming language as your one of your preferred technologies(RxJava,MVP,etc)
//please check this repo for my java clean MVP project
// https://github.com/babakmhz/homefit_android

class MainActivity : DaggerAppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private lateinit var googleApiClient: GoogleApiClient

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private lateinit var locationCallback: LocationCallback

    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var activityBinding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        AndroidInjection.inject(this)

        googleApiClient = GoogleApiClient.Builder(this).addApi(LocationServices.API)
            .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build()
        googleApiClient.connect()

        activityBinding.viewModel = viewModel
        handleObservers()

        viewModel.setLoadingText(getString(R.string.loading_location))
        viewModel.init()

        activityBinding.executePendingBindings()

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

        } else {
            getLastKnownLocation()
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
        val locationRequest = LocationRequest()
        locationRequest.interval = 60000;
        locationRequest.fastestInterval = 60000;
        LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient,
            locationRequest,
            this
        );

    }

    override fun onPause() {
        super.onPause();
        if (googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }


    override fun onResume() {
        super.onResume();
        if (googleApiClient.isConnected) {
            googleApiClient.connect()
        }
    }


    override fun onConnected(p0: Bundle?) {
        AppLogger.i("GOOGLE API LOCATION CONNECTION CONNECTED")
        handleLocationPermission()
//        getLastKnownLocation()
    }

    override fun onConnectionSuspended(p0: Int) {

        AppLogger.i("GOOGLE API LOCATION CONNECTION SUSPENDED")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        AppLogger.i("GOOGLE API LOCATION CONNECTION FAILED")
    }

    override fun onLocationChanged(location: Location?) {
        AppLogger.i("LOCATION FOUND ${location!!.longitude},${location.latitude}")
        viewModel.handleLocationUpdates(location)
    }
}


