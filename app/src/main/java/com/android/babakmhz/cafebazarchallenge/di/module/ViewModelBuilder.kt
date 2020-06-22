package com.android.babakmhz.cafebazarchallenge.di.module

import androidx.lifecycle.ViewModelProvider
import com.android.babakmhz.cafebazarchallenge.utils.DaggerAwareViewModelFactory
import dagger.Binds
import dagger.Module

@Module
internal abstract class ViewModelBuilder {
    @Binds
    internal abstract fun bindViewModelFactory(factory: DaggerAwareViewModelFactory):
            ViewModelProvider.Factory
}