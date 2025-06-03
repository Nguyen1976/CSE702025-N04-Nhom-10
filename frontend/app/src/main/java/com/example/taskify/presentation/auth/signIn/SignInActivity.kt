package com.example.taskify.presentation.auth.signIn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.R
import com.example.taskify.components.AccountTextField
import com.example.taskify.components.ButtonSection
import com.example.taskify.components.PasswordTextField
import com.example.taskify.components.TopTitle
import com.example.taskify.components.isValidEmail
import com.example.taskify.data.themeStorage.ThemeDataStore
import com.example.taskify.presentation.main.MainActivity
import com.example.taskify.presentation.tasktheme.ThemeSectionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        setContent {
            SignInScreen(onBackClick = {finish()})
        }
    }
}

@Composable
fun SignInScreen(
    onBackClick: () -> Unit = {},
    viewModel: SignInViewModel = hiltViewModel()
) {
    val signInState by viewModel.signInState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(signInState) {
        if (signInState is UiState.Error) {
            val errorMessage = (signInState as UiState.Error).message
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(16.dp)
        ) {
            TopTitle(
                title = "Welcome Back!",
                subTitle = "You work faster and structured with Taskify",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(48.dp))

            AccountTextField(
                text = "Email",
                placeholder = "name@example.com",
                value = email,
                onValueChange = { email = it },
                errorText = emailError
            )

            Spacer(modifier = Modifier.height(32.dp))

            PasswordTextField(password = password, onPasswordChange = { password = it }, errorText = passwordError)

            Spacer(modifier = Modifier.weight(1f))

            ButtonSection(
                onClick = {
                    var isValid = true

                    if (email.isBlank()) {
                        emailError = "Email is required"
                        isValid = false
                    } else if (!isValidEmail(email)) {
                        emailError = "Invalid email format"
                        isValid = false
                    }

                    if (password.isBlank()) {
                        passwordError = "Password is required"
                        isValid = false
                    }

                    if (isValid) {
                        viewModel.signIn(email, password)
                    }
                },
                text = "Sign in"
            )
        }

        when (signInState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp,
                        color = Color.White
                    )
                }
            }

            is UiState.Error -> {
                val errorMessage = (signInState as UiState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

            is UiState.Success -> {
                val response = (signInState as UiState.Success).data
                Toast.makeText(context, "Welcome, ${response.username}!", Toast.LENGTH_SHORT).show()

                LaunchedEffect(Unit) {
                    val isThemeChosen = ThemeDataStore.isThemeChosen(context)
                    val intent = if (isThemeChosen) {
                        Intent(context, MainActivity::class.java)
                    } else {
                        Intent(context, ThemeSectionActivity::class.java)
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
            }

            else -> Unit
        }
    }
}