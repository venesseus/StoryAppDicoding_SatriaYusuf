package com.example.mystoryappdicoding.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.mockito.Mockito.*
import org.junit.Assert.*
import com.example.mystoryappdicoding.data.Dummy.storyDummy
import com.example.mystoryappdicoding.data.repo.MapsRepo
import com.example.mystoryappdicoding.data.response.ListStory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository : MapsRepo

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getStoryReturnSuccess() = runTest {
        val observer = Observer<List<ListStory>>{}
        val dummy = storyDummy()

        try {
            val expectedData = MutableLiveData<List<ListStory>>()
            expectedData.value = dummy

            `when`(repository.getStoryLocation("token")).thenReturn(expectedData)

            val actualData = repository.getStoryLocation("token").observeForever(observer)
            verify(repository).getStoryLocation("token")
            assertNotNull(actualData)
        } finally {
            repository.getStoryLocation("token").removeObserver(observer)
        }
    }
}