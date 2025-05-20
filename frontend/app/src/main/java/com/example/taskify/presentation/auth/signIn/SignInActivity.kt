package com.example.taskify.presentation.auth.signIn

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

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
            onValueChange = {email = it},
            errorText = emailError
        )

        Spacer(modifier = Modifier.height(32.dp))

        PasswordTextField(password = password, onPasswordChange = {password = it}, errorText = passwordError)

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

        // Display loading / error / success
        when(signInState) {
            is UiState.Loading -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading...", color = Color.Gray)
            }

            is UiState.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (signInState as UiState.Error).message,
                    color = Color.Red
                )
            }

            is UiState.Success -> {
                val user = (signInState as UiState.Success).data.user
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Welcome, ${user.username}!", color = Color(0xFF24A19C))

                val intent = Intent(context, ThemeSectionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }

            else -> Unit
        }
    }
}