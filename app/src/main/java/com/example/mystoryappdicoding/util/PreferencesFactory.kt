package com.example.mystoryappdicoding.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryappdicoding.data.misc.AuthPreferences
import com.example.mystoryappdicoding.data.repo.AuthRepo
import com.example.mystoryappdicoding.ui.main.MainViewModel
import com.example.mystoryappdicoding.ui.maps.MapsViewModel
import com.example.mystoryappdicoding.ui.upload.UploadViewModel

class PreferencesFactory(
    private val authPreferences: AuthPreferences,
    private val authRepo: AuthRepo,
    private val context: Context,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepo) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(Injection.storyRepo(authPreferences, context)) as T
        }
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(Injection.mapsRepo()) as T
        }
        if (modelClass.isAssignableFrom(PagingModel::class.java)) {
            return PagingModel(Injection.storyRepo(authPreferences, context)) as T
        }
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(Injection.uploadRepo()) as T
        }
        throw IllegalArgumentException("error ${modelClass.name}")
    }
}