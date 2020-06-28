package com.android.babakmhz.cafebazarchallenge.utils

import java.net.InetAddress

object NetworkUtils {
    fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr = InetAddress.getByName("ident.me")
            !ipAddr.equals("")
        } catch (e: Exception) {
            false;
        }
    }
}