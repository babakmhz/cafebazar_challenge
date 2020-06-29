package com.android.babakmhz.cafebazarchallenge.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.babakmhz.cafebazarchallenge.ui.MainViewModel
import com.android.babakmhz.cafebazarchallenge.ui.main.MainUseCase
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class TestViewModel {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: MainViewModel

    @Mock
    lateinit var useCase: MainUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(useCase, Dispatchers.IO)
    }

    @Test
    fun smokeTest() {
        assertTrue(true)
    }

    @Test
    fun `test init viewModel when loading from local source is empty list`() = runBlocking {

        Mockito.`when`(useCase.getLocationFromLocalSource()).thenReturn(emptyList())
        viewModel.locations.observeForever {}
        viewModel.loading.observeForever {
            println(it)
        }

        viewModel.init()
        assertTrue(viewModel.loading.value!!)

    }

}