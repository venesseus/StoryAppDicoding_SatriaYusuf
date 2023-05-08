package com.example.mystoryappdicoding.model

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystoryappdicoding.data.Dummy
import com.example.mystoryappdicoding.data.MainDispatcherRule
import com.example.mystoryappdicoding.data.getOrAwait
import com.example.mystoryappdicoding.data.misc.AuthPreferences
import com.example.mystoryappdicoding.data.repo.AuthRepo
import com.example.mystoryappdicoding.data.response.LoginResponse
import com.example.mystoryappdicoding.data.response.RegisterResponse
import com.example.mystoryappdicoding.util.AuthViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: AuthRepo
    private lateinit var preference: AuthPreferences
    private lateinit var viewModel: AuthViewModel

    private val dummyLogin = Dummy.loginResult()
    private val dummyRegister = Dummy.register()
    private val dummyEmail = "rafflestest@gmail.com"
    private val dummyPassword = "rafflestest"

    @Before
    fun setup() {
        preference = AuthPreferences(Mockito.mock(Context::class.java))
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {

    }

    @Test
    fun loginSuccess() = runTest {
        val expectedData = MutableLiveData<LoginResponse>()
        expectedData.value = dummyLogin
        Mockito.`when`(repository.login(dummyEmail, dummyPassword)).thenReturn(expectedData)

        val actualData = viewModel.login(dummyEmail, dummyPassword).getOrAwait()
        Mockito.verify(repository).login(dummyEmail, dummyPassword)
        assertNotNull(actualData)
        assertEquals(expectedData.value, actualData)
    }

    @Test
    fun registerSuccess() = runTest {
        val expectedData = MutableLiveData<RegisterResponse>()
        expectedData.value = dummyRegister
        Mockito.`when`(repository.register("rafflestest", dummyEmail, dummyPassword)).thenReturn(expectedData)

        val actualData = viewModel.register("rafflestest",dummyEmail, dummyPassword).getOrAwait()
        Mockito.verify(repository).register("rafflestest", dummyEmail, dummyPassword)
        assertNotNull(actualData)
        assertEquals(expectedData.value, actualData)
    }
}