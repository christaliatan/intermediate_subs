package com.dicoding.picodiploma.storyApp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.storyApp.repository.StoryRepository
import com.dicoding.picodiploma.storyApp.view.story.ListStory

class MainViewModel: ViewModel() {
   fun stories(token: String): LiveData<PagingData<ListStory>> = StoryRepository.getStoriesWithPaging(token).cachedIn(viewModelScope)
}