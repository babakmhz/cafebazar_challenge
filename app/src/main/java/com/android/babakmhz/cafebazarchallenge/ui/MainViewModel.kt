package com.android.babakmhz.cafebazarchallenge.ui

import android.location.Location
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.ui.main.LoadingFragment
import com.android.babakmhz.cafebazarchallenge.ui.main.MainFragment
import com.android.babakmhz.cafebazarchallenge.ui.main.MainUseCase
import com.android.babakmhz.cafebazarchallenge.utils.AppLogger
import com.android.babakmhz.cafebazarchallenge.utils.LiveDataWrapper
import kotlinx.coroutines.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val useCase: MainUseCase
) : ViewModel() {

    private val ioDispatcher = Dispatchers.Main
    private val job = SupervisorJob()
    private val uiScope = CoroutineScope(ioDispatcher + job)


    private var _locationPermission = MutableLiveData<Boolean>()
    val locationPermission: LiveData<Boolean> get() = _locationPermission

    private var _selectedLocation = MutableLiveData<LocationModel>()
    val selectedLocation: LiveData<LocationModel> get() = _selectedLocation

    private var _locations = MutableLiveData<LiveDataWrapper<List<LocationModel>>>()
    val locations: LiveData<LiveDataWrapper<List<LocationModel>>> = _locations

    private var _myLocation = MutableLiveData<String>()
    val myLocation: LiveData<String> = _myLocation

    private var _loadingText = MutableLiveData<String>()
    val loadingText: LiveData<String> = _loadingText

    private var _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    private var _currentFragment = MutableLiveData<Fragment>()
    val currentFragment: LiveData<Fragment> = _currentFragment

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading


    fun requestLocationPermission() {
        _locationPermission.value = true
    }

    fun setCurrentFragment(fragment: Fragment) {
        _currentFragment.value = fragment
    }

    fun setLocationPermission(permission: Boolean) {
        _locationPermission.value = permission
    }

    fun setSelectedLocation(location: LocationModel) {
        _selectedLocation.value = location
    }

    fun setLoadingText(loadingText: String) {
        _loadingText.value = loadingText
    }

    fun setLoading(loading: Boolean) {
        _loading.value = loading
    }


    init {
        _locationPermission.value = false
        _loading.value = false
        _error.value = false
    }

    fun init() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val result = useCase.getLocationFromLocalSource()
                if (result.isNotEmpty()) {
                    _loading.postValue(false)
                    _currentFragment.postValue(MainFragment.newInstance())
                    _locations.postValue(LiveDataWrapper.success(result))
                    _myLocation.postValue(useCase.getLastLatLng())
                } else {
                    AppLogger.i("LOCATION FROM LOCAL SOURCE IS EMPTY")
                    if (_currentFragment.value !is LoadingFragment)
                        _currentFragment.postValue(LoadingFragment.newInstance())

                    _locations.postValue(LiveDataWrapper.dataShouldLoadRemotely())
                }
            }
        }
    }


    fun handleLocationUpdates(lastKnownLocation: Location?) {

        _loading.value = true
        _error.value = false

        uiScope.launch {
            withContext(Dispatchers.IO) {
                if (useCase.shouldRequestLocationUpdates(lastKnownLocation)) {
                    try {
                        val response = useCase.getLocationFromRemoteSource(lastKnownLocation)
                        _locations.postValue(LiveDataWrapper.success(response!!))
                        useCase.saveLocations(response)

                        if (_currentFragment.value !is MainFragment)
                            _currentFragment.postValue(MainFragment.newInstance())

                        _myLocation.postValue("${lastKnownLocation?.latitude},${lastKnownLocation?.longitude}")
                    } catch (ex: Exception) {
                        AppLogger.e(ex, "Exception Happened")
                        _error.postValue(true)
                        _locations.postValue(LiveDataWrapper.error(ex))
                    }

                }

                _loading.postValue(false)

            }
        }
    }

}