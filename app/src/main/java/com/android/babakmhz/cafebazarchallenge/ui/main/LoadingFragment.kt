package com.android.babakmhz.cafebazarchallenge.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.babakmhz.cafebazarchallenge.R
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.databinding.FragmentLoadingBinding
import com.android.babakmhz.cafebazarchallenge.ui.MainViewModel
import com.android.babakmhz.cafebazarchallenge.utils.LiveDataWrapper
import com.google.android.material.button.MaterialButton
import dagger.android.AndroidInjection
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_loading.*
import javax.inject.Inject

class LoadingFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    companion object {
        fun newInstance() = LoadingFragment()
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var fragmentLoadingBinding: FragmentLoadingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLoadingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_loading, container, false)

        AndroidInjection.inject(activity)
        viewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        }!!
        fragmentLoadingBinding.viewModel = viewModel

        fragmentLoadingBinding.executePendingBindings()

        viewModel.loadingText.observe(viewLifecycleOwner, loadingTextObserver)
        viewModel.loading.observe(viewLifecycleOwner, loadingObserver)
        viewModel.loadingText.observe(viewLifecycleOwner, loadingTextObserver)

        // in case of crash on material button i was forced to use findViewById
        val bt_requestPermission: MaterialButton =
            fragmentLoadingBinding.root.findViewById(R.id.bt_requestPermission)

        val bt_tryAgain : MaterialButton = fragmentLoadingBinding.root.findViewById(R.id.bt_tryAgain)

        bt_tryAgain.setOnClickListener {
            viewModel.setLoading(true)
            viewModel.init()
        }

        bt_requestPermission.setOnClickListener {
            viewModel.requestLocationPermission()
        }
        viewModel.locations.observe(viewLifecycleOwner, locationObserver)
        return fragmentLoadingBinding.root

    }

    private val loadingObserver = Observer<Boolean> {
        if (it) {
            progress.visibility = View.VISIBLE
            text_loading.text = getString(R.string.loading_location)
            bt_requestPermission.visibility = View.GONE
            bt_tryAgain.visibility = View.GONE
        } else {
            progress.visibility = View.GONE
        }

    }
    private val locationObserver = Observer<LiveDataWrapper<List<LocationModel>>> {
        if (it.responseRESPONSESTATUS == LiveDataWrapper.RESPONSESTATUS.ERROR) {
            text_loading.text = getString(R.string.error)
            bt_tryAgain.visibility = View.VISIBLE
        }
    }
    private val loadingTextObserver = Observer<String> {
        text_loading.text = it
    }
}