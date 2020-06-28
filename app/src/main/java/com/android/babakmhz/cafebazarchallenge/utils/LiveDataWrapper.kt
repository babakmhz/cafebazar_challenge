package com.android.babakmhz.cafebazarchallenge.utils

class LiveDataWrapper<T>(
    val responseRESPONSESTATUS: RESPONSESTATUS,
    val response: T? = null,
    val error: Throwable? = null
) {

    enum class RESPONSESTATUS {
        SUCCESS, ERROR, LOCATION_SHOULD_LOAD_REMOTELY
    }

    companion object {
        fun <T> dataShouldLoadRemotely() = LiveDataWrapper<T>(RESPONSESTATUS.LOCATION_SHOULD_LOAD_REMOTELY)
        fun <T> success(data: T) = LiveDataWrapper<T>(RESPONSESTATUS.SUCCESS, data)
        fun <T> error(err: Throwable) = LiveDataWrapper<T>(RESPONSESTATUS.ERROR, null, err)
    }
}