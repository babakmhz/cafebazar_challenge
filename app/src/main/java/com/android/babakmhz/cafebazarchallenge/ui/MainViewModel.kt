package com.android.babakmhz.cafebazarchallenge.ui

import android.location.Location
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.ui.main.MainUseCase
import com.android.babakmhz.cafebazarchallenge.utils.AppLogger
import com.android.babakmhz.cafebazarchallenge.utils.LiveDataWrapper
import kotlinx.coroutines.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val useCase: MainUseCase,
    ioDispatcher: CoroutineDispatcher
) : ViewModel() {


    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(ioDispatcher + job)

    private var _selectedLocation = MutableLiveData<LocationModel>()
    val selectedLocation: LiveData<LocationModel> get() = _selectedLocation

    private var _locations = MutableLiveData<LiveDataWrapper<List<LocationModel>>>()
    val locations: LiveData<LiveDataWrapper<List<LocationModel>>> = _locations

    private var _myLocation = MutableLiveData<String>()
    val myLocation: LiveData<String> get() = _myLocation

    private var _currentFragment = MutableLiveData<Fragment>()
    val currentFragment: LiveData<Fragment> = _currentFragment

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun setCurrentFragment(fragment: Fragment) {
        _currentFragment.value = fragment
    }

    fun init() {
        _loading.value = true
        ioScope.launch {
            withContext(Dispatchers.IO) {
                val result = useCase.getLocationFromLocalSource()
                if (result.isNotEmpty()) {
                    _loading.postValue(false)
                    _locations.postValue(LiveDataWrapper.success(result))
                } else {
                    _locations.postValue(LiveDataWrapper.dataShouldLoadRemotely())
                }
            }
        }
    }

    fun handleLocationUpdates(lastKnownLocation: Location) {
        _loading.value = true
        ioScope.launch {

            withContext(Dispatchers.IO) {

                if (useCase.shouldRequestLocationUpdates(lastKnownLocation)) {
                    try {
                        val response = useCase.getLocationFromRemoteSource(lastKnownLocation)
                        _locations.postValue(LiveDataWrapper.success(response!!))
                        useCase.saveLocations(response)
                        _loading.postValue(false)
                    } catch (ex: Exception) {
                        AppLogger.e(ex, "Exception Happened")
                        _locations.postValue(LiveDataWrapper.error(ex))
                        _loading.postValue(false)
                    }

                }
            }
        }
    }
}