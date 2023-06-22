package com.mawumbo.storyapp.ui.map

import androidx.lifecycle.ViewModel
import com.mawumbo.storyapp.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryMapViewModel @Inject constructor(
    repository: StoryRepository
) : ViewModel() {

    val storiesWithLocation = repository.getAllStoryWithLocation()

}