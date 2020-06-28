package com.android.babakmhz.cafebazarchallenge.di.module

import android.content.Context
import androidx.room.Room
import com.android.babakmhz.cafebazarchallenge.BuildConfig
import com.android.babakmhz.cafebazarchallenge.data.db.AppDatabase
import com.android.babakmhz.cafebazarchallenge.di.qualifier.ApplicationContext
import com.android.babakmhz.cafebazarchallenge.utils.BASE_URL
import com.android.babakmhz.cafebazarchallenge.utils.DB_NAME
import com.android.babakmhz.cafebazarchallenge.utils.MyApp
import com.android.babakmhz.cafebazarchallenge.utils.TIME_OUT
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
internal abstract class ApplicationModule {

    @Module
    object AppModule {
        @Provides
        @Singleton
        @ApplicationContext
        fun provideAppContext(myApp: MyApp): Context = myApp.applicationContext

        @Provides
        @Singleton
        fun provideRetrofitClient(): Retrofit {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL).build()

        }

        @Provides
        @Singleton
        fun provideAppDatabase(context: Context): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        ).build()


    }
}