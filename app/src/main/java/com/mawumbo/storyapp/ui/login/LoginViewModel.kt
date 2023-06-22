package com.mawumbo.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mawumbo.storyapp.data.repository.StoryRepository
import com.mawumbo.storyapp.model.LoginBody
import com.mawumbo.storyapp.model.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val body = LoginBody(email, password)

            val response = try {
                repository.login(body)
            } catch (exception: HttpException) {
                Log.d("Login", "login: $exception")
                LoginResponse(true, exception.message(), null)
            }

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
            it.copy(error = "Login Failed, $msg")
        }
    }

    fun errorShown() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}

data class LoginUiState(
    val navigateToHome: Boolean? = null,
    val error: String? = null
)