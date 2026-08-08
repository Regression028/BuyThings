package com.example.buythings.domain.repo

import android.net.Uri
import com.example.buythings.common.ResultState
import com.example.buythings.data.models.CartItem
import com.example.buythings.data.models.CategoryData
import com.example.buythings.data.models.BannerData
import com.example.buythings.data.models.ProductData
import com.example.buythings.data.models.UserData
import kotlinx.coroutines.flow.Flow

interface Repo {

    fun registerUserWithEmailAndPassword(userData: UserData, password: String): Flow<ResultState<String>>

    fun loginUserWithEmailAndPassword(email: String, password: String): Flow<ResultState<String>>

    fun getUserById(uid: String): Flow<ResultState<UserData>>

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

    fun getSpecificCategoryItems(categoryName: String): Flow<ResultState<List<ProductData>>>

    fun getAllSuggestedProducts(): Flow<ResultState<List<ProductData>>>

    fun getBanner(): Flow<ResultState<List<BannerData>>>
}