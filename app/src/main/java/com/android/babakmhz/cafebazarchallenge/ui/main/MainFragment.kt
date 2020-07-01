package com.android.babakmhz.cafebazarchallenge.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.babakmhz.cafebazarchallenge.R
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.databinding.FragmentListLocationsBinding
import com.android.babakmhz.cafebazarchallenge.ui.MainViewModel
import com.android.babakmhz.cafebazarchallenge.ui.detail.DetailsFragment
import com.android.babakmhz.cafebazarchallenge.ui.detail.DetailsRecyclerViewAdapter
import com.android.babakmhz.cafebazarchallenge.ui.detail.callBack
import com.android.babakmhz.cafebazarchallenge.utils.AppLogger
import com.android.babakmhz.cafebazarchallenge.utils.LiveDataWrapper
import dagger.android.AndroidInjection
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_list_locations.*
import javax.inject.Inject

//we could use base fragment to do binding jobs also

class MainFragment : DaggerFragment(), callBack {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var fragmentMainBinding: FragmentListLocationsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_locations, container, false)

        AndroidInjection.inject(activity)

        viewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        }!!

        fragmentMainBinding.viewModel = viewModel
        viewModel.locations.observe(viewLifecycleOwner, locationsObserver)
        viewModel.loading.observe(viewLifecycleOwner, loadingObserver)
        viewModel.myLocation.observe(viewLifecycleOwner, userLocationObserver)


        return fragmentMainBinding.root

    }

    private val locationsObserver = Observer<LiveDataWrapper<List<LocationModel>>> {
        if (it.response != null) {
            recycler_items.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL,
                false
            )
            recycler_items.adapter = DetailsRecyclerViewAdapter(context!!, it.response, this)
        }
    }

    private val userLocationObserver = Observer<String> {
        text_latlng.text = String.format("your location coordinates: %s", it)
        AppLogger.i("CHANGING ADDRESS TEXT TO : $it")
    }

    private val loadingObserver = Observer<Boolean> {
        AppLogger.i("LOADING PROGRESS IN MAIN FRAGMENT IS $it")
        if (it) {
            progress.visibility = View.VISIBLE
            text_latlng.text = getString(R.string.determining_your_location)
        } else {
            progress.visibility = View.GONE
            text_latlng.text = viewModel.myLocation.value
        }

    }


    override fun onItemClicked(location: LocationModel) {
        viewModel.setSelectedLocation(location)
        viewModel.setCurrentFragment(DetailsFragment.newInstance())
    }


}