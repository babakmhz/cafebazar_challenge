package com.android.babakmhz.cafebazarchallenge.data.network

import retrofit2.Retrofit
import javax.inject.Inject

class ApiService @Inject constructor(private val retrofit: Retrofit) {

    private val apiHelper: ApiHelper = this.retrofit.create(
        ApiHelper::class.java)

    suspend fun getLocation(
        clientId: String, clientSecret: String, v: String,
        latLng: String, limit: String
    ): ApiResponse {
        return apiHelper.getLocations(clientId, clientSecret, v, latLng, limit)
    }
}