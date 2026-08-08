package com.example.buythings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.buythings.presentation.HomeScreen
import com.example.buythings.presentation.SignUp
import com.example.buythings.presentation.TestUploadScreen
import com.example.buythings.presentation.Utils.RegistrationSuccessDialog
import com.example.buythings.ui.theme.BuyThingsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuyThingsTheme {
                RegistrationSuccessDialog(
                    onGoToHome = {

                    }
                )
            }

        }
    }
}

