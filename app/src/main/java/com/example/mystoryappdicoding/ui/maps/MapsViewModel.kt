package com.example.mystoryappdicoding.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryappdicoding.data.repo.MapsRepo
import com.example.mystoryappdicoding.data.response.ListStory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val mapsRepo: MapsRepo
): ViewModel() {

    fun getStoryLocation(token: String): LiveData<List<ListStory>> =
        mapsRepo.getStoryLocation(token)

    fun getStory(): LiveData<List<ListStory>> = mapsRepo.getStory()
}