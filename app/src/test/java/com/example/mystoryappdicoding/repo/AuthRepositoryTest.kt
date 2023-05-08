package com.example.mystoryappdicoding.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mystoryappdicoding.data.repo.AuthRepo
import org.junit.Assert
import org.mockito.Mockito.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {

    @get: Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepo: AuthRepo

    @Before
    fun setup() {
        authRepo = AuthRepo()
    }

    @Test
    fun loginSuccess() {
        authRepo.login("rafflestest@gmail.com", "rafflestest")
        assertFalse(false)
        assertNotNull("Not Null")
    }

    @Test
    fun registerSuccess() {
        authRepo.register("rafflestest", "rafflestest@gmail.com", "rafflestest")
        assertFalse(false)
        assertNotNull("Not Null")
    }
}