package com.example.buythings.presentation.Utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {

    AlertDialog(

        onDismissRequest = {
            onDismiss()
        },

        containerColor = Color(0xFF555967),

        shape = RoundedCornerShape(20.dp),

        icon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Logout",
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        },

        title = {
            Text(
                text = "LOG OUT",
                color = Color(0xFFFF8585),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },

        text = {
            Text(
                text = "Do you Really\nWant To Logout",
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },

        dismissButton = {
            OutlinedButton(
                onClick = {
                    onDismiss()
                },
                border = BorderStroke(
                    1.dp,
                    Color(0xFFFF8585)
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0xFFFF8585)
                )
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8585)
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Log Out",
                    color = Color.White
                )
            }
        }
    )
}