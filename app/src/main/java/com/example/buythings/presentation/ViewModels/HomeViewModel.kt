package com.example.buythings.presentation.ViewModels
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buythings.common.ResultState
import com.example.buythings.data.models.BannerData
import com.example.buythings.data.models.CategoryData
import com.example.buythings.data.models.ProductData
import com.example.buythings.domain.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
data class HomeScreenState(
    val isLoading: Boolean = false,
    val categories: List<CategoryData> = emptyList(),
    val products: List<ProductData> = emptyList(),
    val suggestedProducts: List<ProductData> = emptyList(),
    val banners: List<BannerData> = emptyList(),
    val errorMessage: String? = null
)




@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    var uiState by mutableStateOf(HomeScreenState())
        private set

    fun getHomeData() {

        viewModelScope.launch {

            uiState = uiState.copy(isLoading = true)

            launch {
                repo.getCategoriesInLimited().collect { result ->

                    when (result) {

                        is ResultState.Loading -> {
                            uiState = uiState.copy(
                                isLoading = true
                            )
                        }

                        is ResultState.Success -> {
                            uiState = uiState.copy(
                                categories = result.data
                            )
                        }

                        is ResultState.Error -> {
                            uiState = uiState.copy(
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }

            launch {
                repo.getProductsInLimited().collect { result ->

                    when (result) {

                        is ResultState.Loading -> {
                            uiState = uiState.copy(
                                isLoading = true
                            )
                        }

                        is ResultState.Success -> {
                            uiState = uiState.copy(
                                products = result.data
                            )
                        }

                        is ResultState.Error -> {
                            uiState = uiState.copy(
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }

            launch {
                repo.getAllSuggestedProducts().collect { result ->

                    when (result) {

                        is ResultState.Loading -> {
                            uiState = uiState.copy(
                                isLoading = true
                            )
                        }

                        is ResultState.Success -> {
                            uiState = uiState.copy(
                                suggestedProducts = result.data
                            )
                        }

                        is ResultState.Error -> {
                            uiState = uiState.copy(
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }

            launch {
                repo.getBanner().collect { result ->

                    when (result) {

                        is ResultState.Loading -> {
                            uiState = uiState.copy(
                                isLoading = true
                            )
                        }

                        is ResultState.Success -> {
                            uiState = uiState.copy(
                                banners = result.data
                            )
                        }

                        is ResultState.Error -> {
                            uiState = uiState.copy(
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }
}