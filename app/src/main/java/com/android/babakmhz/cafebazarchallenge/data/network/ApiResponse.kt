package com.android.babakmhz.cafebazarchallenge.data.network

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("response") val response: Response
)


data class Meta(
    @SerializedName("code") val code: Int,
    @SerializedName("requestId") val request_id: String
)

data class Response(
    @SerializedName("headerLocation") val headerLocation: String,
    @SerializedName("headerFullLocation") val headerFullLocation: String,
    @SerializedName("headerLocationGranularity") val headerLocationGranularity: String
    , @SerializedName("groups") val groups: Groups
)

data class Groups(
    @SerializedName("type") val type: String,
    @SerializedName("items") val items: List<Items>
)

data class Items(@SerializedName("reasons") val reasons: Reasons)

data class Reasons(@SerializedName("venue") val venue: Venue)

data class Venue(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String, @SerializedName("location") val location: Location
)

data class Location(
    @SerializedName("address") val address: String,
    @SerializedName("crossStreet") val crossStreet: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("lng") val lng: String,
    @SerializedName("distance") val distance: Int,
    @SerializedName("postalCode") val postalCode: String,
    @SerializedName("cc") val cc: String,
    @SerializedName("city") val city: String,
    @SerializedName("country") val country: String,
    @SerializedName("formattedAddress") val formattedAddress: List<String>,
    @SerializedName("categories") val categories: List<Category>
)

data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("pluralName") val pluralName: String,
    @SerializedName("shortName") val shortName: String,
    @SerializedName("icon") val icon: Icon
)

data class Icon(
    @SerializedName("prefix") val prefix: String,
    @SerializedName("suffix") val suffix: String
)