package com.android.babakmhz.cafebazarchallenge.di.module

import com.android.babakmhz.cafebazarchallenge.data.ApiService
import com.android.babakmhz.cafebazarchallenge.data.BASE_URL
import com.android.babakmhz.cafebazarchallenge.data.TIME_OUT
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

        @JvmStatic
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

        @JvmStatic
        @Provides
        @Singleton
        fun provideAppApiService(retrofit: Retrofit): ApiService {
            return ApiService(retrofit)
        }



    }
}