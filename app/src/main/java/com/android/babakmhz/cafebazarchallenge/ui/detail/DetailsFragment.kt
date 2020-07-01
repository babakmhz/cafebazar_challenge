package com.android.babakmhz.cafebazarchallenge.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.babakmhz.cafebazarchallenge.R
import com.android.babakmhz.cafebazarchallenge.databinding.FragmentLocationDetailsBinding
import com.android.babakmhz.cafebazarchallenge.ui.MainViewModel
import dagger.android.AndroidInjection
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class DetailsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var fragmentLocationDetailsBinding: FragmentLocationDetailsBinding

    companion object {
        fun newInstance() = DetailsFragment()
    }


    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLocationDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_location_details, container, false)

        AndroidInjection.inject(activity)

        viewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        }!!

        fragmentLocationDetailsBinding.viewModel = viewModel


        return fragmentLocationDetailsBinding.root
    }


}