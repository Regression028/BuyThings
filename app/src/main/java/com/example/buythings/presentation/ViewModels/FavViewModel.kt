package com.example.buythings.presentation.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buythings.common.ResultState
import com.example.buythings.data.models.ProductData
import com.example.buythings.domain.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

data class FavoriteScreenState(
    val isLoading: Boolean = false,
    val favoriteProducts: List<ProductData> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    var uiState by mutableStateOf(FavoriteScreenState())
        private set

    fun getAllFavorites() {

        viewModelScope.launch {

            repo.getAllFav().collect { result ->

                when (result) {

                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            favoriteProducts = result.data,
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

    fun addToFavorite(productData: ProductData) {

        viewModelScope.launch {

            repo.addToFav(productData).collect { result ->

                when (result) {

                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            isLoading = false
                        )

                        getAllFavorites()
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
        uiState = FavoriteScreenState()
    }
}