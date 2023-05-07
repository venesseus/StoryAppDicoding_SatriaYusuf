package com.example.mystoryappdicoding.data.repo

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.mystoryappdicoding.data.misc.AuthPreferences
import com.example.mystoryappdicoding.data.misc.StoryRemoteMediator
import com.example.mystoryappdicoding.data.response.ListStory
import com.example.mystoryappdicoding.data.retrofit.ApiService
import com.example.mystoryappdicoding.data.room.StoryDatabase
import javax.inject.Inject

class MainRepo @Inject constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    val authPreferences: AuthPreferences
) {

    fun getStory(): LiveData<PagingData<ListStory>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, authPreferences),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStory()
            }
        ).liveData
    }
}