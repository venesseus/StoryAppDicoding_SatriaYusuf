package com.example.mystoryappdicoding.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystoryappdicoding.data.Dummy
import com.example.mystoryappdicoding.data.MainDispatcherRule
import com.example.mystoryappdicoding.data.getOrAwait
import com.example.mystoryappdicoding.data.repo.UploadRepo
import com.example.mystoryappdicoding.data.response.UploadResponse
import com.example.mystoryappdicoding.ui.upload.UploadViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UploadViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: UploadRepo
    private lateinit var viewModel: UploadViewModel

    private val token = "token"
    private val dummy = Dummy.uploadStory()
    private val image = Dummy.imageDummy()
    private val desc = Dummy.descDummy()
    private val lat = 40.7143528
    private val lon = -74.0059731

    @Before
    fun setup() {
        viewModel = UploadViewModel(repository)
    }

    @Test
    fun addStorySuccess() = runTest {
        val expectedData = MutableLiveData<UploadResponse>()
        expectedData.postValue(dummy)
        `when`(repository.uploadStory(token, image, desc, lat, lon)).thenReturn(expectedData)

        val actualData = viewModel.uploadStory(token, image, desc, lat, lon).getOrAwait()
        verify(repository).uploadStory(token, image, desc, lat, lon)

        assertNotNull(actualData)
        assertEquals(actualData, expectedData.value)
    }
}