package com.example.buythings.presentation

import androidx.credentials.CredentialManager
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buythings.data.models.UserData
import android.app.Activity

import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignUp(
    authViewModel: AuthViewModel = hiltViewModel(),
    onSignUpSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val credentialManager = CredentialManager.create(context)
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val uiState = authViewModel.uiState

    // React to state changes: show errors, navigate on success
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                Toast.makeText(context, "SUCCESS STATE REACHED", Toast.LENGTH_LONG).show()

                onSignUpSuccess()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, uiState.message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            else -> Unit
        }
    }
    fun signInWithGoogle() {

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(
                context.getString(com.example.buythings.R.string.default_web_client_id)
            )
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Dispatchers.Main).launch {

            try {

                val result = credentialManager.getCredential(
                    context,
                    request
                )

                val credential = result.credential

                if (
                    credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {

                    val googleCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    val idToken = googleCredential.idToken

                    authViewModel.loginWithGoogle(idToken)

                }

            } catch (e: GoogleIdTokenParsingException) {

                Toast.makeText(
                    context,
                    "Google sign-in failed",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {

                Toast.makeText(
                    context,
                    e.localizedMessage ?: "Google sign-in failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text ="Create A New Account",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))

            SignupTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(15.dp))

            SignupTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(15.dp))

            SignupTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(15.dp))

            SignupTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                icon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(15.dp))

            SignupTextField(
                value = password,
                onValueChange = { password = it },
                label = "Create Password",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(15.dp))

            SignupTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(27.dp))

            Button(
                onClick = {
                    if (
                        firstName.isBlank() ||
                        lastName.isBlank() ||
                        email.isBlank() ||
                        phone.isBlank() ||
                        password.isBlank() ||
                        confirmPassword.isBlank()
                    ) {
                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                    }
                    else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show()
                    }
                    else if (password != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        // TODO: firstName, lastName, phone aren't stored by Firebase Auth itself —
                        // once Firestore is set up, we'll save these as a user profile document
                        // right after this call succeeds.
                        authViewModel.signUp(
                            UserData(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                phoneNumber = phone
                            ),
                            password
                        )
                    }
                },
                enabled = uiState !is AuthUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8A98)
                )
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "SignUp",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(27.dp))

            Row {
                Text(
                    "Already have an account? ",
                    color = Color.White
                )

                Text(
                    "Login",
                    color = Color(0xFFFF8A98),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    ) { onLoginClick() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray
                )

                Text(
                    " OR ",
                    color = Color.White
                )

                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedButton(
                onClick = {
                    signInWithGoogle()

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.example.buythings.R.drawable.google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    "Log in with Google",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SignupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        leadingIcon = {
            Icon(icon, contentDescription = null)
        },
        singleLine = true,
        visualTransformation = if (isPassword)
            PasswordVisualTransformation()
        else
            androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.LightGray,
            unfocusedLabelColor = Color.LightGray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedLeadingIconColor = Color.White,
            unfocusedLeadingIconColor = Color.White
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SignUpScreenPreview() {
    MaterialTheme {
        //SignUp()
    }
}
