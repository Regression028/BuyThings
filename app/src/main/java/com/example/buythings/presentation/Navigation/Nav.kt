package com.example.buythings.presentation.navigation
import com.example.buythings.presentation.CheckoutScreen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.buythings.presentation.CartScreen
import com.example.buythings.presentation.EachProductDetailsScreen
import com.example.buythings.presentation.HomeScreen
import com.example.buythings.presentation.SignUp
import com.example.buythings.presentation.ProfileScreen


@Composable
fun Nav() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HomeScreen
    ) {

        composable<Routes.HomeScreen> {

            HomeScreen(
                onProductClick = { productId ->

                    navController.navigate(
                        Routes.EachProductDetailsScreen(
                            productID = productId
                        )
                    )
                },

                onCartClick = {

                    navController.navigate(
                        Routes.CartScreen
                    )
                },

                onProfileClick = {

                    navController.navigate(
                        Routes.ProfileScreen
                    )
                }
            )
        }

        composable<Routes.EachProductDetailsScreen> { backStackEntry ->

            val route =
                backStackEntry.toRoute<Routes.EachProductDetailsScreen>()

            EachProductDetailsScreen(
                productID = route.productID,
                onBackClick = {
                    navController.popBackStack()
                }
            )

        }
        // CART
        // CART

        composable<Routes.CartScreen> {

            CartScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCheckoutClick = {
                    navController.navigate(
                        Routes.CheckoutScreen
                    )
                }
            )
        }
        composable<Routes.CheckoutScreen> {

            CheckoutScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<Routes.SignUpScreen> {

            SignUp(
                onSignUpSuccess = {
                    navController.navigate(
                        Routes.HomeScreen
                    ) {
                        popUpTo<Routes.SignUpScreen> {
                            inclusive = true
                        }
                    }
                },

                onLoginClick = {

                    navController.navigate(
                        Routes.LoginScreen
                    )
                }
            )
        }
        composable<Routes.ProfileScreen> {

            ProfileScreen(
                onHomeClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}