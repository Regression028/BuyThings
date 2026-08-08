package com.example.shopping.presentation.navigation

import kotlinx.serialization.Serializable

sealed class SubNavigation {

    @Serializable
    object LoginSignUpScreen : SubNavigation()

    @Serializable
    object MainHomeScreen : SubNavigation()
}


sealed class Routes {

    @Serializable
    object LoginScreen : Routes()

    @Serializable
    object SignUpScreen : Routes()

    @Serializable
    object HomeScreen : Routes()

    @Serializable
    object ProfileScreen : Routes()

    @Serializable
    object WishListScreen : Routes()

    @Serializable
    object CartScreen : Routes()

    @Serializable
    data class CheckoutScreen(
        val productId: String
    ) : Routes()

    @Serializable
    object PayScreen : Routes()

    @Serializable
    object SeeAllProductScreen : Routes()

    @Serializable
    data class EachProductDetailsScreen(
        val productID: String
    ) : Routes()

    @Serializable
    object AllCategoriesScreen : Routes()

    @Serializable
    data class EachCategoryItemsScreen(
        val categoryName: String
    ) : Routes()
}