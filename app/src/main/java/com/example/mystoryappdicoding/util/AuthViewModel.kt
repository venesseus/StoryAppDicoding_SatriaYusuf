package com.example.mystoryappdicoding.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryappdicoding.data.repo.AuthRepo
import com.example.mystoryappdicoding.data.response.LoginResult
import com.example.mystoryappdicoding.data.response.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo,
) : ViewModel() {

    val regMessage: LiveData<Event<String>>
        get() = authRepo.regMessage

    val logMessage: LiveData<Event<String>>
        get() = authRepo.logMessage

    val loginUser: LiveData<LoginResult> = authRepo.loginUser
    val registerUser: LiveData<RegisterResponse> = authRepo.registerUser
    val isEnabled: LiveData<Boolean> = authRepo.isEnabled
    val isLoading: LiveData<Boolean> = authRepo.isLoading

    fun login(email: String, password: String) =
        authRepo.login(email, password)

    fun register(username: String, email: String, password: String) =
        authRepo.register(username, email, password)
}