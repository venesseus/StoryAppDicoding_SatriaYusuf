package com.example.mystoryappdicoding.util

import android.content.Context
import com.example.mystoryappdicoding.data.misc.AuthPreferences
import com.example.mystoryappdicoding.data.repo.MainRepo
import com.example.mystoryappdicoding.data.repo.MapsRepo
import com.example.mystoryappdicoding.data.repo.UploadRepo
import com.example.mystoryappdicoding.data.retrofit.ApiConfig
import com.example.mystoryappdicoding.data.room.StoryDatabase

object Injection {

    fun storyRepo(authPreferences: AuthPreferences, context: Context): MainRepo {
        val storyDatabase = StoryDatabase.getInstance(context)
        val apiService = ApiConfig.getApiService()
        return MainRepo(storyDatabase, apiService, authPreferences)
    }

    fun mapsRepo(): MapsRepo {
        return MapsRepo()
    }

    fun uploadRepo(): UploadRepo {
        return UploadRepo()
    }
}