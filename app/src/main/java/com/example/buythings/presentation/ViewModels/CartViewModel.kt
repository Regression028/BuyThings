package com.example.buythings.presentation.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buythings.common.ResultState
import com.example.buythings.data.models.CartItem
import com.example.buythings.domain.models.CreateOrderResponse
import com.example.buythings.domain.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

data class CartScreenState(
    val isLoading: Boolean = false,
    val isPaymentLoading: Boolean = false,
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val errorMessage: String? = null,
    val paymentErrorMessage: String? = null,
    val razorpayOrderResponse: CreateOrderResponse? = null,
    val isPaymentVerified: Boolean = false,
    val verifiedPaymentId: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    var uiState by mutableStateOf(CartScreenState())
        private set

    fun getCart() {

        viewModelScope.launch {

            repo.getCart().collect { result ->

                when (result) {

                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {

                        val cartItems = result.data

                        calculateCartTotal(cartItems)

                        uiState = uiState.copy(
                            cartItems = cartItems,
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

    fun addToCart(cartItem: CartItem) {

        viewModelScope.launch {

            repo.addToCart(cartItem).collect { result ->

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

                        getCart()
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

    fun updateQuantity(
        cartItemId: String,
        quantity: Int
    ) {

        if (quantity <= 0) {
            removeFromCart(cartItemId)
            return
        }

        viewModelScope.launch {

            repo.updateCartQuantity(
                cartItemId,
                quantity
            ).collect { result ->

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

                        getCart()
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

    fun removeFromCart(cartItemId: String) {

        viewModelScope.launch {

            repo.removeFromCart(cartItemId).collect { result ->

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

                        getCart()
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

    fun clearCart() {

        viewModelScope.launch {

            repo.clearCart().collect { result ->

                when (result) {

                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            cartItems = emptyList(),
                            subtotal = 0.0,
                            deliveryFee = 0.0,
                            discount = 0.0,
                            total = 0.0,
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

    fun createPaymentOrder(amount: Double) {
        viewModelScope.launch {
            repo.createPaymentOrder(amount).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isPaymentLoading = true,
                            paymentErrorMessage = null
                        )
                    }
                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            isPaymentLoading = false,
                            razorpayOrderResponse = result.data,
                            paymentErrorMessage = null
                        )
                    }
                    is ResultState.Error -> {
                        uiState = uiState.copy(
                            isPaymentLoading = false,
                            paymentErrorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun verifyPayment(orderId: String, paymentId: String, signature: String) {
        viewModelScope.launch {
            repo.verifyPayment(orderId, paymentId, signature).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        uiState = uiState.copy(
                            isPaymentLoading = true,
                            paymentErrorMessage = null
                        )
                    }
                    is ResultState.Success -> {
                        uiState = uiState.copy(
                            isPaymentLoading = false,
                            isPaymentVerified = true,
                            verifiedPaymentId = paymentId
                        )
                        clearCart()
                    }
                    is ResultState.Error -> {
                        uiState = uiState.copy(
                            isPaymentLoading = false,
                            paymentErrorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun consumeRazorpayOrderResponse() {
        uiState = uiState.copy(razorpayOrderResponse = null)
    }

    fun resetPaymentState() {
        uiState = uiState.copy(
            isPaymentLoading = false,
            paymentErrorMessage = null,
            razorpayOrderResponse = null,
            isPaymentVerified = false,
            verifiedPaymentId = null
        )
    }

    private fun calculateCartTotal(
        cartItems: List<CartItem>
    ) {

        val subtotal = cartItems.sumOf {
            it.priceAtAddTime * it.quantity
        }

        val deliveryFee = if (subtotal > 0) 50.0 else 0.0

        val discount = if (subtotal >= 2000) 200.0 else 0.0

        val total = subtotal + deliveryFee - discount

        uiState = uiState.copy(
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            total = total
        )
    }

    fun resetState() {
        uiState = CartScreenState()
    }
}
