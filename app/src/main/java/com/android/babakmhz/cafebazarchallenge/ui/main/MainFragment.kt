package com.android.babakmhz.cafebazarchallenge.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.babakmhz.cafebazarchallenge.R
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.databinding.FragmentListLocationsBinding
import com.android.babakmhz.cafebazarchallenge.ui.MainViewModel
import com.android.babakmhz.cafebazarchallenge.ui.detail.DetailsRecyclerViewAdapter
import com.android.babakmhz.cafebazarchallenge.ui.detail.callBack
import com.android.babakmhz.cafebazarchallenge.utils.LiveDataWrapper
import kotlinx.android.synthetic.main.fragment_list_locations.*
import javax.inject.Inject

//we could use base fragment to do binding jobs also

class MainFragment : Fragment(), callBack {

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

        viewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        }!!

        fragmentMainBinding.viewModel = viewModel
        viewModel.locations.observe(viewLifecycleOwner, locationsObserver)

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

    override fun onItemClicked(location: LocationModel) {
        viewModel.setSelectedLocation(location)
        viewModel.setCurrentFragment(DetailsFragment.newInstance())
    }


}