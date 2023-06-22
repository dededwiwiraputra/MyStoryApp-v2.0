package com.mawumbo.storyapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mawumbo.storyapp.data.repository.StoryRepository
import com.mawumbo.storyapp.model.RegisterBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val body = RegisterBody(name, email, password)

            val response = repository.register(body)

            if (!response.error) {
                navigateToLogin()
            } else {
                showError(response.message)
            }

        }
    }

    fun navigateToLogin() {
        _uiState.update {
            it.copy(navigateToLogin = true)
        }
    }

    fun navigatedToLogin() {
        _uiState.update {
            it.copy(navigateToLogin = null)
        }
    }

    fun showError(msg: String) {
        _uiState.update {
            it.copy(error = "Register Failed, $msg")
        }
    }

    fun errorShown() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}


data class RegisterUiState(
    val navigateToLogin: Boolean? = null,
    val error: String? = null
)