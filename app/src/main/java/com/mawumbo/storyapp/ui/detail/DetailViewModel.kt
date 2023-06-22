package com.mawumbo.storyapp.ui.detail

import androidx.lifecycle.*
import com.mawumbo.storyapp.data.repository.StoryRepository
import com.mawumbo.storyapp.data.resource.Resource
import com.mawumbo.storyapp.model.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: StoryRepository
) : ViewModel() {
    private val storyId: String? = savedStateHandle["storyId"]

    private val _uiState = MutableLiveData<DetailStoryUiState>()
    val uiState: LiveData<DetailStoryUiState> = _uiState

    init {
        if (storyId != null) {
            getStory(storyId)
        }
    }

    private fun getStory(storyId: String) {
        viewModelScope.launch {
            _uiState.postValue(
                DetailStoryUiState.Loading
            )

            when (val resource = repository.getStory(storyId)) {
                is Resource.Success -> {
                    _uiState.postValue(
                        DetailStoryUiState.Success(resource.data.story)
                    )
                }
                is Resource.Failure -> {
                    _uiState.postValue(
                        DetailStoryUiState.Error(resource.message)
                    )
                }
            }
        }

    }
}

sealed interface DetailStoryUiState {
    object Loading : DetailStoryUiState
    data class Success(val story: Story) : DetailStoryUiState
    data class Error(val message: String) : DetailStoryUiState
}