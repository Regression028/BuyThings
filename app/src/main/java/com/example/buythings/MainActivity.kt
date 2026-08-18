package com.example.buythings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.buythings.presentation.HomeScreen
import com.example.buythings.presentation.ProfileScreen
import com.example.buythings.presentation.SignUp
import com.example.buythings.presentation.navigation.Nav
import com.example.buythings.ui.theme.BuyThingsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuyThingsTheme {
               Nav()
            }
        }
    }
}

