package com.example.mystoryappdicoding.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryappdicoding.data.repo.UploadRepo
import com.example.mystoryappdicoding.data.response.ListStory
import com.example.mystoryappdicoding.data.response.UploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val uploadRepo: UploadRepo
) : ViewModel() {

    val story: LiveData<List<ListStory>> = uploadRepo.stories
    val storyResponse: LiveData<UploadResponse> = uploadRepo.storyResponse
    val isLoading: LiveData<Boolean> = uploadRepo.isLoading
    val isEnabled: LiveData<Boolean> = uploadRepo.isEnabled

    fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody,
        lat: Double? = null,
        lon: Double? = null
    ) = uploadRepo.uploadStory(token, image, desc, lat, lon)
}