package com.android.babakmhz.cafebazarchallenge.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiHelper {

    @GET("venues/explore")
    suspend fun getLocations(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("v") v: String,
        @Query("ll") latLng: String,
        @Query("limit") responseLimit: String
    ): ApiResponse
}