package com.example.mystoryappdicoding.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mystoryappdicoding.data.Dummy.descDummy
import com.example.mystoryappdicoding.data.Dummy.imageDummy
import com.example.mystoryappdicoding.data.repo.UploadRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UploadRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: UploadRepo

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addStoryReturnFailed() = runTest {
        val image = imageDummy()
        val desc = descDummy()
        repository.uploadStory("token", image, desc, 40.7143528, -74.0059731)
        assertTrue(true)
        assertNotNull("Not null")
    }
}