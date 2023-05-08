package com.example.mystoryappdicoding.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.example.mystoryappdicoding.data.Dummy
import com.example.mystoryappdicoding.data.Dummy.dummyStoryNull
import com.example.mystoryappdicoding.data.Dummy.storyDummy
import com.example.mystoryappdicoding.data.MainDispatcherRule
import com.example.mystoryappdicoding.data.getOrAwait
import com.example.mystoryappdicoding.data.misc.PagingSource
import com.example.mystoryappdicoding.data.repo.MainRepo
import com.example.mystoryappdicoding.data.response.ListStory
import com.example.mystoryappdicoding.ui.main.MainAdapter.Companion.DIFF_CALLBACK
import com.example.mystoryappdicoding.ui.main.MainAdapter.Companion.noopListUpdateCallback
import com.example.mystoryappdicoding.ui.main.MainViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

@RunWith(MockitoJUnitRunner::class)
class MainRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: MainRepo

    @Before
    fun setup(){
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getStoryReturnSuccess() = runTest {
        val dummy = storyDummy()
        val data = PagingSource.snapShot(dummy)
        val viewModel = MainViewModel(repository)

        val expectedData = MutableLiveData<PagingData<ListStory>>()
        expectedData.value = data
        `when`(repository.getStory()).thenReturn(expectedData)
        viewModel.getStory()

        val actualData = viewModel.getStory().getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Main
        )
        differ.submitData(actualData)

        assertNotNull(differ.snapshot())
        assertEquals(dummy,differ.snapshot())
        assertEquals(dummy.size,differ.snapshot().size)
        assertEquals(dummy[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getStoryWithNoDataReturnSuccess() = runTest {
        val dummy = dummyStoryNull
        val data = PagingSource.snapShot(dummy)
        val viewModel = MainViewModel(repository)
        val expectData = MutableLiveData<PagingData<ListStory>>()
        expectData.value = data

        `when`(repository.getStory()).thenReturn(expectData)
        viewModel.getStory()

        val actualData = viewModel.getStory().getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Main
        )
        differ.submitData(actualData)

        assertEquals(0, differ.snapshot().size)
    }
}