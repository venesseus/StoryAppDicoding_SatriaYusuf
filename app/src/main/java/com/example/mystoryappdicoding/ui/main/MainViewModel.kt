package com.example.mystoryappdicoding.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.mystoryappdicoding.data.repo.MainRepo
import com.example.mystoryappdicoding.data.response.ListStory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepo: MainRepo
): ViewModel() {

    val loading = MutableLiveData<Boolean>()

    fun getStory(): LiveData<PagingData<ListStory>> =
        mainRepo.getStory()
}