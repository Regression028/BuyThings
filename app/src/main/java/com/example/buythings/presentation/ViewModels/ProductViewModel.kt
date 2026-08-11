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

data class ProductScreenState(
    val isLoading: Boolean = false,
    val products: List<ProductData> = emptyList(),
    val selectedProduct: ProductData? = null,
    val categoryProducts: List<ProductData> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    var uiState by mutableStateOf(ProductScreenState())
        private set

    fun getAllProducts() {

        viewModelScope.launch {

            repo.getAllProducts().collect { result ->

                when (result) {

                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            products = result.data,
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

    fun getProductById(productId: String) {

        viewModelScope.launch {

            repo.getProductById(productId).collect { result ->

                when (result) {

                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            selectedProduct = result.data,
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

    fun getCategoryProducts(categoryName: String) {

        viewModelScope.launch {

            repo.getSpecificCategoryItems(categoryName).collect { result ->

                when (result) {

                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            categoryProducts = result.data,
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
        uiState = ProductScreenState()
    }
}