package com.example.buythings.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.buythings.presentation.HomeScreen

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
                }
            )
        }

        composable<Routes.EachProductDetailsScreen> { backStackEntry ->

            val route =
                backStackEntry.toRoute<Routes.EachProductDetailsScreen>()

            Routes.EachProductDetailsScreen(
                productID = route.productID
            )
        }
    }
}