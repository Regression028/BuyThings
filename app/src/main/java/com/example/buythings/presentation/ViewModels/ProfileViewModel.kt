package com.example.buythings.presentation.ViewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buythings.common.ResultState
import com.example.buythings.data.models.UserData
import com.example.buythings.data.models.UserDataParent
import com.example.buythings.domain.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val isLoading: Boolean = false,
    val userData: UserDataParent? = null,
    val profileImageUrl: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    var uiState by mutableStateOf(ProfileScreenState())
        private set

    fun getUserProfile(uid: String) {

        viewModelScope.launch {

            repo.getUserById(uid).collect { result ->

                when (result) {

                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            userData = result.data,
                            isLoading = false
                        )
                    }

                    is ResultState.Error -> {
                        uiState = uiState.copy(
                            errorMessage = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun updateProfile(userData: UserData) {

        viewModelScope.launch {

            repo.updateUserData(userData).collect { result ->

                when (result) {

                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            isLoading = false,
                            userData = UserDataParent(
                                userData = userData
                            )
                        )
                    }

                    is ResultState.Error -> {
                        uiState = uiState.copy(
                            errorMessage = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {

        viewModelScope.launch {

            repo.userProfileImage(uri).collect { result ->

                when (result) {

                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            profileImageUrl = result.data,
                            isLoading = false
                        )
                    }

                    is ResultState.Error -> {
                        uiState = uiState.copy(
                            errorMessage = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun resetState() {
        uiState = ProfileScreenState()
    }
}