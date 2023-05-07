package com.example.mystoryappdicoding.data.misc

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mystoryappdicoding.data.remote.RemoteKeys
import com.example.mystoryappdicoding.data.response.ListStory
import com.example.mystoryappdicoding.data.retrofit.ApiService
import com.example.mystoryappdicoding.data.room.StoryDatabase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator @Inject constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
): RemoteMediator<Int, ListStory>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStory>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: initialPageIndex
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeysForFirstItem(state)
                val prevKeys = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKeys
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }
        return try {
            val token: String = authPreferences.getToken().first()
            val response = apiService.getStory("Bearer $token", state.config.pageSize, page)
            val endOfPaginationReach = response.listStory.isEmpty()

            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDatabase.remoteKeysDao().delRemote()
                    storyDatabase.storyDao().deleteStory()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReach) null else page + 1
                val keys = response.listStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                storyDatabase.remoteKeysDao().addAll(keys)
                response.listStory.forEach { story ->
                    val item = ListStory(
                        story.photoUrl,
                        story.createdAt,
                        story.name,
                        story.description,
                        story.id,
                        story.lat,
                        story.lon
                    )
                    storyDatabase.storyDao().addStory(item)
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReach)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeysForFirstItem(state: PagingState<Int, ListStory>): RemoteKeys? {
        return state.pages.firstOrNull() {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteId(data.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStory>): RemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStory>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteKeysDao().getRemoteId(id)
            }
        }
    }

    companion object {
        const val initialPageIndex = 1
    }
}