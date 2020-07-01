package com.android.babakmhz.cafebazarchallenge.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.babakmhz.cafebazarchallenge.R
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.databinding.MainActivityBinding
import com.android.babakmhz.cafebazarchallenge.ui.main.MainFragment
import com.android.babakmhz.cafebazarchallenge.utils.AppLogger
import com.android.babakmhz.cafebazarchallenge.utils.LOCATION_PERMISSION_CODE
import com.android.babakmhz.cafebazarchallenge.utils.LiveDataWrapper
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*
import javax.inject.Inject

//as i noticed java programming language as your one of your preferred technologies(RxJava,MVP,etc)
//please check this repo for my java clean MVP project
// https://github.com/babakmhz/homefit_android

class MainActivity : DaggerAppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private lateinit var googleApiClient: GoogleApiClient
    private var backPressedOnce = false

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

        AndroidInjection.inject(this)

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        googleApiClient = GoogleApiClient.Builder(this).addApi(LocationServices.API)
            .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build()
        googleApiClient.connect()

        activityBinding.viewModel = viewModel

        handleObservers()

        viewModel.setLocationPermission(checkIfAppHasPermissions())
        viewModel.setLoadingText(getString(R.string.accept_permission))

        viewModel.init()

        activityBinding.executePendingBindings()

    }


    private fun handleObservers() {
        viewModel.currentFragment.observe(this, fragmentsObserver)
        viewModel.locationPermission.observe(this, requestPermissionObserver)
        viewModel.locations.observe(this, locationsObserver)
    }

    private val fragmentsObserver = Observer<Fragment> {
        if (it != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, it)
                .commitNow()
        }
    }

    private fun checkIfAppHasPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            return false

        return true

    }

    private fun handleLocationPermission() {
        if (!checkIfAppHasPermissions()) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE)
            if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                viewModel.setLoadingText(getString(R.string.loading_location))
                getLastKnownLocation()
            } else {
                viewModel.setLocationPermission(false)
                viewModel.setLoadingText(getString(R.string.accept_permission))
            }
    }


    private val requestPermissionObserver = Observer<Boolean> {
        if (it) {
            handleLocationPermission()
        }
    }

    private val locationsObserver = Observer<LiveDataWrapper<List<LocationModel>>> {
        if (it.responseRESPONSESTATUS == LiveDataWrapper.RESPONSESTATUS.LOCATION_SHOULD_LOAD_REMOTELY) {
            try {
                requestLocationUpdates()
            } catch (e: Exception) {
                AppLogger.e("FAILED TO REQUEST LOCATION UPDATES")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest()
        locationRequest.smallestDisplacement = 100F
        LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient,
            locationRequest,
            this
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        locationClient.lastLocation.addOnSuccessListener {
            AppLogger.i("THIS IS LAST KNOWN LOCATION ${it.latitude},${it.longitude}")
            viewModel.handleLocationUpdates(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        googleApiClient.disconnect()
    }

    override fun onPause() {
        super.onPause();
        if (googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    override fun onBackPressed() {
        if (backPressedOnce) {
            finish()
        }

        if (viewModel.currentFragment.value is MainFragment
        ) {
            showSnackBar(
                getString(R.string.press_back_again)
            )
            backPressedOnce = true
            Handler().postDelayed({
                backPressedOnce = false
            }, 1500)
        } else {
            viewModel.setCurrentFragment(MainFragment.newInstance())
        }

    }

    private fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(
            container,
            message, Snackbar.LENGTH_LONG
        )
        val sbView = snackbar.view
        val textView = sbView
            .findViewById<View>(R.id.snackbar_text) as TextView
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        snackbar.show()
    }

    override fun onResume() {
        super.onResume();
        if (!googleApiClient.isConnected) {
            googleApiClient.connect()
        }
    }


    override fun onConnected(p0: Bundle?) {
        AppLogger.i("GOOGLE API LOCATION CONNECTION CONNECTED")
        if (checkIfAppHasPermissions())
            requestLocationUpdates()
    }

    override fun onConnectionSuspended(p0: Int) {
        AppLogger.i("GOOGLE API LOCATION CONNECTION SUSPENDED")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        AppLogger.i("GOOGLE API LOCATION CONNECTION FAILED")
    }

    override fun onLocationChanged(location: Location?) {
        AppLogger.i("LOCATION FOUND ${location!!.latitude},${location.longitude}")
        viewModel.handleLocationUpdates(location)
    }
}
