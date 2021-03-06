package com.android.babakmhz.cafebazarchallenge.di.module

import androidx.lifecycle.ViewModel
import com.android.babakmhz.cafebazarchallenge.di.Scope.PerActivity
import com.android.babakmhz.cafebazarchallenge.di.qualifier.ActivityContext
import com.android.babakmhz.cafebazarchallenge.ui.MainActivity
import com.android.babakmhz.cafebazarchallenge.ui.MainViewModel
import com.android.babakmhz.cafebazarchallenge.ui.detail.DetailsFragment
import com.android.babakmhz.cafebazarchallenge.ui.main.LoadingFragment
import com.android.babakmhz.cafebazarchallenge.ui.main.MainFragment
import com.android.babakmhz.cafebazarchallenge.utils.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.multibindings.IntoMap

@Module
internal abstract class ActivityModule {

    companion object {
        @Provides
        @JvmStatic
        @ActivityContext
        @PerActivity
        fun getActivityContext(activity: DaggerAppCompatActivity) = activity

    }

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun provideMainViewModelFactory(mainViewModel: MainViewModel): ViewModel


    @ContributesAndroidInjector
    internal abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun mainFragment(): MainFragment

    @ContributesAndroidInjector
    internal abstract fun loadingFragment(): LoadingFragment

    @ContributesAndroidInjector
    internal abstract fun detailsFragment(): DetailsFragment
}
