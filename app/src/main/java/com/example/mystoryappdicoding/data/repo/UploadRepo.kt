package com.example.mystoryappdicoding.data.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mystoryappdicoding.data.response.ListStory
import com.example.mystoryappdicoding.data.response.UploadResponse
import com.example.mystoryappdicoding.data.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadRepo {

    private val _stories = MutableLiveData<List<ListStory>>()
    val stories: LiveData<List<ListStory>> = _stories

    private val _storyResponse = MutableLiveData<UploadResponse>()
    val storyResponse: LiveData<UploadResponse> = _storyResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled: LiveData<Boolean> = _isEnabled

    fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody,
        lat: Double?,
        lon: Double?,
    ): LiveData<UploadResponse> {

        _isLoading.value = true
        _isEnabled.value = false
        ApiConfig.getApiService().uploadStoryLocation(token, image, desc, lat, lon)
            .enqueue(object : Callback<UploadResponse> {
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {
                    _isLoading.value = false
                    _isEnabled.value = true
                    if (response.isSuccessful) {
                        _storyResponse.postValue(response.body())
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        return _storyResponse
    }

    companion object {
        private const val TAG = "UploadRepo"
    }
}
