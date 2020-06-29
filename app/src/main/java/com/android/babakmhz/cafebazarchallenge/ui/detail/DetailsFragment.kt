package com.android.babakmhz.cafebazarchallenge.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.android.babakmhz.cafebazarchallenge.R
import com.android.babakmhz.cafebazarchallenge.ui.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailsFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = DetailsFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_location_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

}