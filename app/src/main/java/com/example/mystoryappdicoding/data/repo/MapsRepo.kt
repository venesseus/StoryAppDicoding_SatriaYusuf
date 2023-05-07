package com.example.mystoryappdicoding.data.repo

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mystoryappdicoding.data.response.ListStory
import com.example.mystoryappdicoding.data.response.StoryResponse
import com.example.mystoryappdicoding.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsRepo {

    private val _mapStory = MutableLiveData<List<ListStory>>()

    fun getStoryLocation(token : String): LiveData<List<ListStory>> {
        ApiConfig.getApiService().getStoriesLocation(token, 1)
            .enqueue(object : Callback<StoryResponse> {
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    if (response.isSuccessful) {
                        _mapStory.postValue(response.body()?.listStory)
                    }
                }
                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        return _mapStory
    }

    fun getStory(): LiveData<List<ListStory>> {
        return _mapStory
    }
}