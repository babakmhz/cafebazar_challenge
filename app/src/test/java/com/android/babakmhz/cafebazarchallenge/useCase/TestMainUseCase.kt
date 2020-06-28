package com.android.babakmhz.cafebazarchallenge.useCase

import android.location.Location
import com.android.babakmhz.cafebazarchallenge.data.db.AppDatabase
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.data.db.LocationsDao
import com.android.babakmhz.cafebazarchallenge.data.network.*
import com.android.babakmhz.cafebazarchallenge.data.prefs.AppPrefs
import com.android.babakmhz.cafebazarchallenge.ui.main.MainUseCase
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class TestMainUseCase {

    @Mock
    lateinit var apiService: ApiService

    @Mock
    lateinit var db: AppDatabase

    @Mock
    lateinit var prefs: AppPrefs

    @Mock
    lateinit var meta: Meta

    @Mock
    lateinit var apiResponse: Response

    @Mock
    lateinit var lastKnownLocation: Location

    @Mock
    lateinit var locationsDao: LocationsDao

    lateinit var mainUseCase: MainUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(db.locationsDao()).thenReturn(locationsDao)
        mainUseCase = MainUseCase(db, prefs, apiService)
    }

    @Test
    fun smokeTest() {
        assertTrue(true)
    }

    @Test
    fun `test calling api for getting location from remote source returns error code != 200 ,method should return null`() =
        runBlocking {

            val _response = ApiResponse(meta, apiResponse)

            Mockito.`when`(_response.meta.code).thenReturn(400)

            Mockito.`when`(
                apiService.getLocation(
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyInt()
                )
            ).thenReturn(_response)


            mainUseCase = MainUseCase(db, prefs, apiService)

            assertNull(mainUseCase.getLocationFromRemoteSource(lastKnownLocation))
        }

    @Test
    fun `test calling api for getting location from remote source returns code == 200 , but no locations Found`() =
        runBlocking {

            val groups = mock<Groups>()
            val _apiDep = Response("", "", "", groups)
            val _response = ApiResponse(meta, _apiDep)

            Mockito.`when`(_response.response.groups.items).thenReturn(emptyList())

            Mockito.`when`(_response.meta.code).thenReturn(200)
            Mockito.`when`(
                apiService.getLocation(
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyInt()
                )
            ).thenReturn(_response)

            assertEquals(mainUseCase.getLocationFromRemoteSource(lastKnownLocation)!!.size, 0)
        }

    @Test
    fun `test calling api for getting location from remote source returns code == 200 , should return valid listOf locationModel`() =
        runBlocking {
            val id = "venueId"
            val name = "venueName"
            val location = Location(
                "address", "crossStreet", lat = "12321", lng = "13543", distance = 100,
                postalCode = "postalCode", cc = "cc", city = "city", country = "country",
                formattedAddress = listOf("this", "is", "formatted Address"),
                categories = listOf(
                    Category(
                        "id", "name", "pluralName",
                        "shortName",
                        Icon("something", ".png")
                    )
                )
            )
            val groups = mock<Groups>()
            val _apiDep = Response("headerLocation", "HeaderFullLocation", "SomethingElse", groups)
            val _response = ApiResponse(meta, _apiDep)

            val venue = Venue(id, name, location)
            val reasons = Reasons(venue)

            // size of founded locations is 3
            Mockito.`when`(_response.response.groups.items)
                .thenReturn(listOf(Items(reasons), Items(reasons), Items(reasons)))

            Mockito.`when`(_response.meta.code).thenReturn(200)

            Mockito.`when`(
                apiService.getLocation(
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyInt()
                )
            ).thenReturn(_response)

            val locationModels = mainUseCase.getLocationFromRemoteSource(lastKnownLocation)!!
            assertNotEquals(locationModels.size, 0)
            assertEquals(locationModels.size, 3)

            locationModels.forEachIndexed { index, result ->
                val apiModel = _response.response.groups.items[index].reasons.venue
                assertEquals(result.name, apiModel.name)
                assertEquals(result.location.address, apiModel.location.address)
                assertEquals(result.location.city, apiModel.location.city)
                assertEquals(result.location.country, apiModel.location.country)
                assertEquals(
                    result.location.formattedAddress,
                    convert(apiModel.location.formattedAddress)
                )
                assertEquals(result.location.distance, apiModel.location.distance)
                assertEquals(result.location.lat, apiModel.location.lat)
                assertEquals(result.location.lng, apiModel.location.lng)
                assertEquals(
                    result.location.icon,
                    "${apiModel.location.categories[0].icon.prefix}${location.categories[0].icon.suffix}"
                )
                println(result.location.icon)
            }
        }


    @Test
    fun `test shouldRequestLocationUpdates should return true if app first time opened or lastKnownLocation is empty`() {
        runBlocking {
            Mockito.`when`(prefs.getLastKnownLocation()).thenReturn("")
            Mockito.`when`(prefs.getNotFirstTimeAppOpened()).thenReturn(true)

            assertTrue(mainUseCase.shouldRequestLocationUpdates(lastKnownLocation))
        }
    }

    @Test
    fun `test shouldRequestLocationUpdates should return true if no locations found in prefs and lastKnownLocation from android system is null`() {
        runBlocking {
            Mockito.`when`(prefs.getLastKnownLocation()).thenReturn("")
            assertTrue(mainUseCase.shouldRequestLocationUpdates(null))
        }
    }

    @Test
    fun `test shouldRequestLocationUpdates should return true if given lastKnownLocation From system differs with prefs more then 100meters`() {
        runBlocking {
            val lat1 = "40.7243"
            val lng1 = "-74.0018"
            val lat2 = "40.8244"
            val lng2 = "-74.0020"

            val locationModel = Mockito.mock(LocationModel::class.java)
            // these spots differ is about 11.3km
            Mockito.`when`(prefs.getLastKnownLocation()).thenReturn("")
            Mockito.`when`(lastKnownLocation.latitude).thenReturn(lat1.toDouble())
            Mockito.`when`(lastKnownLocation.longitude).thenReturn(lng1.toDouble())
            Mockito.`when`(prefs.getLastKnownLocation()).thenReturn("$lat2,$lng2")
            Mockito.`when`(db.locationsDao().getAllLocations())
                .thenReturn(listOf(locationModel))


            assertFalse(db.locationsDao().getAllLocations().isEmpty())
            assertTrue(mainUseCase.shouldRequestLocationUpdates(lastKnownLocation))
        }
    }

    @Test
    fun `test shouldRequestLocationUpdates should return false if given lastKnownLocation From system differs with prefs less then 100meters`() {
        runBlocking {
            val lat1 = "40.7243"
            val lng1 = "-74.0018"
            val lat2 = lat1
            val lng2 = lng1

            val locationModel = Mockito.mock(LocationModel::class.java)
            // these spots differ is about 11.3km
            Mockito.`when`(prefs.getLastKnownLocation()).thenReturn("")
            Mockito.`when`(lastKnownLocation.latitude).thenReturn(lat1.toDouble())
            Mockito.`when`(lastKnownLocation.longitude).thenReturn(lng1.toDouble())
            Mockito.`when`(prefs.getLastKnownLocation()).thenReturn("$lat2,$lng2")
            Mockito.`when`(db.locationsDao().getAllLocations())
                .thenReturn(listOf(locationModel))


            assertFalse(db.locationsDao().getAllLocations().isEmpty())
            assertFalse(mainUseCase.shouldRequestLocationUpdates(lastKnownLocation))
        }
    }

    //this function is copy of object in LocationModel
    private fun convert(addresses: List<String>): String {
        var result = ""
        for (address in addresses) {
            result += address
        }
        return result
    }
}