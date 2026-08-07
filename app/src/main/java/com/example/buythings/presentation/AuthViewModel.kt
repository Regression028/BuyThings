package com.example.buythings.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buythings.data.auth.AuthRepository
import kotlinx.coroutines.launch

/**
 * What the UI needs to know at any given moment:
 * are we loading, did it succeed, or did it fail (and with what message)?
 */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set

    fun signUp(email: String, password: String) {
        uiState = AuthUiState.Loading
        viewModelScope.launch {
            try {
                repository.signUp(email, password)
                uiState = AuthUiState.Success
            } catch (e: Exception) {
                uiState = AuthUiState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun login(email: String, password: String) {
        uiState = AuthUiState.Loading
        viewModelScope.launch {
            try {
                repository.login(email, password)
                uiState = AuthUiState.Success
            } catch (e: Exception) {
                uiState = AuthUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    /**
     * Call this after showing an error/success message, so the state
     * doesn't stay stuck and re-trigger the same message on recomposition.
     */
    fun resetState() {
        uiState = AuthUiState.Idle
    }
}