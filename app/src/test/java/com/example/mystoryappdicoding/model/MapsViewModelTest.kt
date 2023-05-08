package com.example.mystoryappdicoding.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystoryappdicoding.data.Dummy
import com.example.mystoryappdicoding.data.getOrAwait
import com.example.mystoryappdicoding.data.repo.MapsRepo
import com.example.mystoryappdicoding.data.response.ListStory
import com.example.mystoryappdicoding.ui.maps.MapsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.junit.Assert.*
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: MapsRepo
    private lateinit var viewModel: MapsViewModel
    private val dummy = Dummy.storyDummy()

    @Before
    fun setup() {
        viewModel = MapsViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getStoryWithMapsReturnSuccess() = runTest {
        val expectedData = MutableLiveData<List<ListStory>>()
        expectedData.postValue(dummy)
        `when`(repository.getStoryLocation("token")).thenReturn(expectedData)

        val actualData = viewModel.getStoryLocation("token").getOrAwait()
        verify(repository).getStoryLocation("token")

        assertNotNull(actualData)
        assertEquals(actualData, expectedData.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getMarkerMapsReturnSuccess() = runTest {
        val expectedData = MutableLiveData<List<ListStory>>()
        expectedData.postValue(dummy)
        `when`(repository.getStory()).thenReturn(expectedData)

        val actualData = viewModel.getStory().getOrAwait()
        verify(repository).getStory()

        assertNotNull(actualData)
        assertEquals(actualData, expectedData.value)
    }
}