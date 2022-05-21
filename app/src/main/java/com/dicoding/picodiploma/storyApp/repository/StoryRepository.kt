package com.dicoding.picodiploma.storyApp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.storyApp.api.ApiConfig
import com.dicoding.picodiploma.storyApp.view.story.ListStory
import com.dicoding.picodiploma.storyApp.view.story.StoryPagingSource

object StoryRepository {

    fun getStoriesWithPaging(token: String): LiveData<PagingData<ListStory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(ApiConfig.instances, token)
            }
        ).liveData
    }

}