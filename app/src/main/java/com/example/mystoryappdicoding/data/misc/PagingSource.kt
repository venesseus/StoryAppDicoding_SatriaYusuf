package com.example.mystoryappdicoding.data.misc

import androidx.paging.PagingData
import com.example.mystoryappdicoding.data.retrofit.ApiService
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mystoryappdicoding.data.response.ListStory
import kotlinx.coroutines.flow.first

class PagingSource(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences,
) : PagingSource<Int, ListStory>() {

    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {

        return try {
            val position = params.key ?: initialPageIndex
            val token = authPreferences.getToken().first()
            val response = apiService.getStory("Bearer $token", params.loadSize, position)
            val listStory = response.listStory

            LoadResult.Page(
                listStory,
                prevKey = if (position == initialPageIndex) null else position - 1,
                nextKey = if (listStory.isEmpty()) null else position + 1
            )
        }
        catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    companion object {
        const val initialPageIndex = 1

        fun snapShot(items: List<ListStory>) : PagingData<ListStory> {
            return PagingData.from(items)
        }
    }
}