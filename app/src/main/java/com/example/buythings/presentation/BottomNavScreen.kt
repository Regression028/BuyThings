package com.example.buythings.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.buythings.presentation.BottomNavItem

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {

    val bottomNavItems = listOf(

        BottomNavItem(
            name = "Home",
            icon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),

        BottomNavItem(
            name = "Wishlist",
            icon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.Favorite
        ),

        BottomNavItem(
            name = "Cart",
            icon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart
        ),

        BottomNavItem(
            name = "Profile",
            icon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    NavigationBar {

        bottomNavItems.forEachIndexed { index, item ->

            NavigationBarItem(
                selected = selectedItem == index,

                onClick = {
                    onItemSelected(index)
                },

                icon = {
                    androidx.compose.material3.Icon(
                        imageVector = if (selectedItem == index) {
                            item.icon
                        } else {
                            item.unselectedIcon
                        },
                        contentDescription = item.name
                    )
                },

                label = {
                    Text(text = item.name)
                }
            )
        }
    }
}