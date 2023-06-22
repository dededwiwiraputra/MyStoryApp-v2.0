package com.mawumbo.storyapp.ui.addstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mawumbo.storyapp.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState

    fun uploadImage(photo: File, description: String) {
        viewModelScope.launch {
            val response = repository.uploadImage(photo, description)
            if (!response.error) {
                navigateToHome()
            } else {
                showError(response.message)
            }
        }
    }

    fun navigateToHome() {
        _uiState.update {
            it.copy(navigateToHome = true)
        }
    }

    fun navigatedToHome() {
        _uiState.update {
            it.copy(navigateToHome = null)
        }
    }

    fun showError(msg: String) {
        _uiState.update {
            it.copy(error = "Upload Failed, $msg")
        }
    }

    fun errorShown() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}

data class UploadUiState(
    val navigateToHome: Boolean? = null,
    val error: String? = null
)