package com.example.mystoryappdicoding.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystoryappdicoding.data.repo.MainRepo
import com.example.mystoryappdicoding.data.response.ListStory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PagingModel @Inject constructor(
    mainRepo: MainRepo
) : ViewModel() {

    val getStory: LiveData<PagingData<ListStory>> =
        mainRepo.getStory().cachedIn(viewModelScope)
}