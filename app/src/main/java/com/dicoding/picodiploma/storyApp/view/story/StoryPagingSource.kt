package com.dicoding.picodiploma.storyApp.view.story

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.picodiploma.storyApp.api.ApiService

class StoryPagingSource(private val apiService: ApiService, private val token: String) : PagingSource<Int,ListStory>(){
    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        Log.d(TAG, "load: ")
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStoriesPaging(token, page, params.loadSize)
            val stories = responseData.body()?.listStory
            if(stories != null) {
                Log.d(TAG, "load: not null $stories")
                LoadResult.Page(
                    data = stories,
                    prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                    nextKey = if (stories.isNullOrEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("No Data"))
            }
        } catch (exception: Exception) {
            Log.e(TAG, "load: error ${exception.message}")
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val TAG = "StoryPagingSource"
    }
}