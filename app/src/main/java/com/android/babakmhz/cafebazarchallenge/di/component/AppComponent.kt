package com.android.babakmhz.cafebazarchallenge.di.component

import com.android.babakmhz.cafebazarchallenge.di.module.ActivityModule
import com.android.babakmhz.cafebazarchallenge.di.module.ApplicationModule
import com.android.babakmhz.cafebazarchallenge.di.module.ViewModelBuilder
import com.android.babakmhz.cafebazarchallenge.utils.MyApp
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class
        , ApplicationModule::class
        , ViewModelBuilder::class
        , ApplicationModule.AppModule::class
        , ActivityModule::class]
)
interface AppComponent : AndroidInjector<MyApp> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MyApp>()
}