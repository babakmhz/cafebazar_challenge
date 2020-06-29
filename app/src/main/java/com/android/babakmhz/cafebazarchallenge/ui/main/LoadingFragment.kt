package com.android.babakmhz.cafebazarchallenge.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.babakmhz.cafebazarchallenge.R
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.databinding.FragmentLoadingBinding
import com.android.babakmhz.cafebazarchallenge.ui.MainViewModel
import com.android.babakmhz.cafebazarchallenge.utils.LiveDataWrapper
import kotlinx.android.synthetic.main.fragment_loading.*
import javax.inject.Inject

class LoadingFragment : Fragment() {
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_locations, container, false)

        viewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        }!!
        fragmentLoadingBinding.viewModel = viewModel
        fragmentLoadingBinding.executePendingBindings()

        bt_requestPermission.setOnClickListener {
            viewModel.init()
        }
        return fragmentLoadingBinding.root

    }


}