package com.example.taskify.presentation.auth.signUp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskify.components.AccountTextField
import com.example.taskify.components.PasswordTextField
import com.example.taskify.components.TopTitle
import com.example.taskify.components.isValidEmail
import com.example.taskify.data.local.TokenManager
import com.example.taskify.data.remote.ApiClient
import com.example.taskify.data.repository.AuthRepository

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenManager = TokenManager(applicationContext)
        val viewModel = SignUpViewModel(AuthRepository(ApiClient.authApi, tokenManager))

        setContent {
            SignUpScreen(onBackClick = {finish()}, viewModel = viewModel)
        }
    }
}

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit = {},
    viewModel: SignUpViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
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
            onValueChange = {email = it},
            errorText = emailError
        )

        Spacer(modifier = Modifier.height(32.dp))

        AccountTextField(
            text = "Username",
            placeholder = "Enter your username",
            value = username,
            onValueChange = {username = it},
            errorText = usernameError
        )

        Spacer(modifier = Modifier.height(32.dp))

        PasswordTextField(password = password, onPasswordChange = {password = it}, errorText = passwordError)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                var isValid = true

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
                }

                if (password.isBlank()) {
                    passwordError = "Password is required"
                    isValid = false
                }

                if (isValid) {
                    viewModel.signUp()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF24A19C)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Sign Up",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}