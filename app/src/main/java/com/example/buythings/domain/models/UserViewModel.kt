package com.example.buythings.presentation.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buythings.common.ResultState
import com.example.buythings.data.models.UserAddress
import com.example.buythings.data.models.UserData
import com.example.buythings.domain.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import android.net.Uri

data class UserScreenState(
    val isLoading: Boolean = false,
    val userData: UserData? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    var uiState by mutableStateOf(UserScreenState())
        private set

    fun getUserData() {

        viewModelScope.launch {

            val uid = repoUid()

            if (uid == null) {
                uiState = uiState.copy(
                    errorMessage = "User is not logged in"
                )
                return@launch
            }

            repo.getUserById(uid).collect { result ->

                when (result) {

                    is ResultState.Loading -> {

                        uiState = uiState.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }

                    is ResultState.Success -> {

                        uiState = uiState.copy(
                            userData = result.data.userData,
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    is ResultState.Error -> {

                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun addAddress(address: UserAddress) {

        viewModelScope.launch {

            val currentUser = uiState.userData

            if (currentUser != null) {

                val updatedUser = currentUser.copy(
                    addresses = currentUser.addresses + address
                )

                updateUser(updatedUser)

            } else {

                val uid = repoUid()

                if (uid == null) {

                    uiState = uiState.copy(
                        errorMessage = "User is not logged in"
                    )

                    return@launch
                }

                repo.getUserById(uid).collect { result ->

                    when (result) {

                        is ResultState.Loading -> {

                            uiState = uiState.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }

                        is ResultState.Success -> {

                            val currentUserData = result.data.userData

                            val updatedUser = currentUserData.copy(
                                addresses = currentUserData.addresses + address
                            )

                            repo.updateUserData(updatedUser).collect { updateResult ->

                                when (updateResult) {

                                    is ResultState.Loading -> {

                                        uiState = uiState.copy(
                                            isLoading = true
                                        )
                                    }

                                    is ResultState.Success -> {

                                        uiState = uiState.copy(
                                            userData = updatedUser,
                                            isLoading = false,
                                            errorMessage = null
                                        )
                                    }

                                    is ResultState.Error -> {

                                        uiState = uiState.copy(
                                            isLoading = false,
                                            errorMessage = updateResult.message
                                        )
                                    }
                                }
                            }
                        }

                        is ResultState.Error -> {

                            uiState = uiState.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateUser(userData: UserData) {

        viewModelScope.launch {

            repo.updateUserData(userData).collect { result ->

                when (result) {

                    is ResultState.Loading -> {

                        uiState = uiState.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }

                    is ResultState.Success -> {

                        uiState = uiState.copy(
                            userData = userData,
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    is ResultState.Error -> {

                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = result.message
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
                            isLoading = true,
                            errorMessage = null
                        )
                    }

                    is ResultState.Success -> {

                        val currentUser = uiState.userData

                        if (currentUser != null) {

                            uiState = uiState.copy(
                                userData = currentUser.copy(
                                    profileImage = result.data
                                ),
                                isLoading = false,
                                errorMessage = null
                            )

                        } else {

                            uiState = uiState.copy(
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }

                    is ResultState.Error -> {

                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun repoUid(): String? {
        return com.google.firebase.auth.FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid
    }
    fun logout() {

        com.google.firebase.auth.FirebaseAuth
            .getInstance()
            .signOut()

        resetState()
    }

    fun resetState() {
        uiState = UserScreenState()
    }
}