package com.example.taskify.presentation.auth.signUp

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.components.AccountTextField
import com.example.taskify.components.PasswordTextField
import com.example.taskify.components.TopTitle
import com.example.taskify.components.isValidEmail
import com.example.taskify.data.themeStorage.ThemeDataStore
import com.example.taskify.presentation.auth.dashboard.DashboardActivity
import com.example.taskify.presentation.main.MainActivity
import com.example.taskify.presentation.tasktheme.ThemeSectionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SignUpScreen(onBackClick = { finish() })
        }
    }
}

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit = {},
    viewModel: SignUpViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val signUpState by viewModel.signUpState.collectAsState()
    val context = LocalContext.current
    var lastToastTime by remember { mutableStateOf(0L) }

    LaunchedEffect(signUpState) {
        if (signUpState is UiState.Success) {
            Toast.makeText(context, "Account registration successful. Please sign in to continue using the service!", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            (context as? Activity)?.finish()
            viewModel.resetSignUpState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TopTitle(
                title = "Create account",
                subTitle = "Create your account and feel the benefits",
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

            AccountTextField(
                text = "Username",
                placeholder = "Enter your username",
                value = username,
                onValueChange = { username = it },
                errorText = usernameError
            )

            Spacer(modifier = Modifier.height(32.dp))

            PasswordTextField(
                text = "Password",
                placeholder = "Enter your password",
                password = password,
                onPasswordChange = { password = it },
                errorText = passwordError
            )

            Spacer(modifier = Modifier.weight(1f))

            // Error
            LaunchedEffect(signUpState) {
                if (signUpState is UiState.Error) {
                    val message = (signUpState as UiState.Error).message

                    val currentTime = System.currentTimeMillis()
                    if(currentTime - lastToastTime > 2000) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        lastToastTime = currentTime
                    }

                    viewModel.resetSignUpState()
                }
            }

            Button(
                onClick = {
                    var isValid = true
                    val usernameRegex = Regex("^[a-zA-Z0-9_]+$")

                    emailError = null
                    usernameError = null
                    passwordError = null

                    if (email.isBlank()) {
                        emailError = "Email is required"
                        isValid = false
                    } else if (!isValidEmail(email)) {
                        emailError = "Invalid email format"
                        isValid = false
                    }

                    if (username.isBlank()) {
                        usernameError = "Username is required"
                        isValid = false
                    } else if (username.any { it.isWhitespace() }) {
                        usernameError = "Username must not contain whitespace"
                        isValid = false
                    } else if (!username.matches(usernameRegex)) {
                        usernameError = "Username can only contain letters, digits and underscores"
                        isValid = false
                    }

                    if (password.isBlank()) {
                        passwordError = "Password is required"
                        isValid = false
                    }

                    if (!isValidPassword(password)) {
                        passwordError = "Password must be at least 8 characters,\n" + "include a digit and an uppercase letter!"
                        isValid = false
                    }

                    if (isValid) {
                        viewModel.signUp(email, username, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF24A19C)
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = signUpState !is UiState.Loading
            ) {
                Text(
                    "Sign Up",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

        // Loading
        if (signUpState is UiState.Loading) {
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
    }
}

fun isValidPassword(password: String): Boolean {
    val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
    return passwordRegex.matches(password)
}