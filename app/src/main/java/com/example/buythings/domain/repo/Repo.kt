package com.example.buythings.domain.repo

import android.net.Uri
import com.example.buythings.common.ResultState
import com.example.buythings.data.models.CartItem
import com.example.buythings.data.models.CategoryData
import com.example.buythings.data.models.BannerData
import com.example.buythings.data.models.ProductData
import com.example.buythings.data.models.UserData
import com.example.buythings.data.models.UserDataParent
import kotlinx.coroutines.flow.Flow

interface Repo {

    fun registerUserWithEmailAndPassword(userData: UserData, password: String): Flow<ResultState<String>>

    fun loginUserWithEmailAndPassword(email: String, password: String): Flow<ResultState<String>>

    fun getUserById(uid: String): Flow<ResultState<UserDataParent>>

    fun updateUserData(userData: UserData): Flow<ResultState<String>>

    fun userProfileImage(uri: Uri): Flow<ResultState<String>>

    fun getCategoriesInLimited(): Flow<ResultState<List<CategoryData>>>

    fun getProductsInLimited(): Flow<ResultState<List<ProductData>>>

    fun getAllProducts(): Flow<ResultState<List<ProductData>>>

    fun getProductById(productId: String): Flow<ResultState<ProductData>>

    fun addToCart(cartItem: CartItem): Flow<ResultState<String>>

    fun addToFav(productData: ProductData): Flow<ResultState<String>>

    fun getAllFav(): Flow<ResultState<List<ProductData>>>

    fun getCart(): Flow<ResultState<List<CartItem>>>

    fun getAllCategories(): Flow<ResultState<List<CategoryData>>>

    fun getCheckOut(): Flow<ResultState<List<CartItem>>>

    fun getSpecificCategoryItems(categoryName: String): Flow<ResultState<List<ProductData>>>

    fun getAllSuggestedProducts(): Flow<ResultState<List<ProductData>>>

    fun getBanner(): Flow<ResultState<List<BannerData>>>

    fun updateCartQuantity(
        cartItemId: String,
        quantity: Int
    ): Flow<ResultState<String>>

    fun removeFromCart(
        cartItemId: String
    ): Flow<ResultState<String>>

    fun clearCart(): Flow<ResultState<String>>
    fun loginWithGoogle(idToken: String): Flow<ResultState<String>>

    fun createPaymentOrder(amount: Double): Flow<ResultState<com.example.buythings.domain.models.CreateOrderResponse>>
    fun verifyPayment(orderId: String, paymentId: String, signature: String): Flow<ResultState<com.example.buythings.domain.models.VerifyPaymentResponse>>
}