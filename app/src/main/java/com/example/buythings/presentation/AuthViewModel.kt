package com.example.buythings.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buythings.common.ResultState
import com.example.buythings.data.models.UserData
import com.example.buythings.domain.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * What the UI needs to know at any given moment:
 * this is authviewmodel
 * are we loading, did it succeed, or did it fail (and with what message)?
 */
sealed class AuthUiState { //sealed class holds data
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
/** this annotation is used to tell hilt that this viewmodel will be needing Hilt and dependencies
 @Inject Constrcutor means provide the dependecnises whatever is neeeded inside the constructore which is Repo ,all the functions which are needeed //of repo are provides   */

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set
/** var uiState holds the state of authentication and initially it is idle*/
/** Signup screen in Ui calls signUp function */

    fun signUp(userData: UserData, password: String) {
        viewModelScope.launch {
            repo.registerUserWithEmailAndPassword(userData, password)
                .collect { result ->

                    when (result) {

                        is ResultState.Loading -> {
                            uiState = AuthUiState.Loading
                        }

                        is ResultState.Success -> {
                            uiState = AuthUiState.Success
                        }

                        is ResultState.Error -> {
                            uiState = AuthUiState.Error(result.message)
                        }
                    }
                }
        }
    }
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            repo.loginWithGoogle(idToken)
                .collect { result ->

                    when (result) {

                        is ResultState.Loading -> {
                            uiState = AuthUiState.Loading
                        }

                        is ResultState.Success -> {
                            uiState = AuthUiState.Success
                        }

                        is ResultState.Error -> {
                            uiState = AuthUiState.Error(result.message)
                        }
                    }
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repo.loginUserWithEmailAndPassword(email, password)
                .collect { result ->

                    when (result) {

                        is ResultState.Loading -> {
                            uiState = AuthUiState.Loading
                        }

                        is ResultState.Success -> {
                            uiState = AuthUiState.Success
                        }

                        is ResultState.Error -> {
                            uiState = AuthUiState.Error(result.message)
                        }
                    }
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