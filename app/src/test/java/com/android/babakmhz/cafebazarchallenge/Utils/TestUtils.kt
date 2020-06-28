package com.android.babakmhz.cafebazarchallenge.Utils

import com.android.babakmhz.cafebazarchallenge.utils.AppUtils
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.net.InetAddress


@RunWith(JUnit4::class)
class TestUtils {


    @Before
    fun setUp() {

    }

    // that's not real test actually :) , i'm just testing if this method working in my case
    @Test
    fun `test network is connected via valid url should pass`() {
        val ipAddr = InetAddress.getByName("google.com");
        print(ipAddr)
        assert(!ipAddr.equals(""))
    }

    @Test
    fun `test get lat lng from valid lat lng string should return valid array with size of 2`() {
        val validLatLng = "40.7243,-74.0018"
        val expected = AppUtils.getLatLngFromString(validLatLng)
        val latitude = "40.7243"
        val longitude = "-74.0018"
        assertNotNull(expected)
        assertEquals(expected!!.latitude, latitude.toDouble(), 0.0)
        assertEquals(expected.longitude, longitude.toDouble(), 0.0)

    }

    @Test
    fun `test get lat lng from invalid lat lng string should return null`() {
        val validLatLng = "somethingDummy"
        val expected = AppUtils.getLatLngFromString(validLatLng)
        assertNull(expected)
    }


    @Test
    fun `test measuring distance between 2 valid lat lng should return correct calculation`() {

        //on online calculations expected value is 11.13 KM


        val lat1 = "40.7243"
        val lng1 = "-74.0018"
        val lat2 = "40.8244"
        val lng2 = "-74.0020"

        val latlng1 = LatLng(lat1.toDouble(), lng1.toDouble())
        val latlng2 = LatLng(lat2.toDouble(), lng2.toDouble())

        val result = AppUtils.measureDistanceBetweenTwoLocations(latlng1, latlng2)

        assertNotNull(result)

        assertEquals(result, 11130)

    }

    @Test
    fun `test measuring distance between invalid lat lng should return null`() {
        val lat2 = "40.8244"
        val lng2 = "-74.0020"

        val latlng1 = null
        val latlng2 = LatLng(lat2.toDouble(), lng2.toDouble())

        val result = AppUtils.measureDistanceBetweenTwoLocations(latlng1, latlng2)

        assertNull(result)


    }
}