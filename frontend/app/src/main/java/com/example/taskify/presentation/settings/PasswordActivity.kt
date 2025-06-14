package com.example.taskify.presentation.settings

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.R
import com.example.taskify.components.ButtonSection
import com.example.taskify.components.PasswordTextField
import com.example.taskify.data.themeStorage.ThemeDataStore
import com.example.taskify.data.viewmodel.UpdatePasswordState
import com.example.taskify.data.viewmodel.UserViewModel
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.presentation.main.toColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password)

        setContent {
            val context = LocalContext.current
            val themeFlow = ThemeDataStore.getSavedTheme(context)
            val theme by themeFlow.collectAsState(initial = ThemeOption.Teal)

            ChangePasswordScreen(
                theme = theme ?: ThemeOption.Teal,
                onBackClick = {finish()}
            )
        }
    }
}

@Composable
fun ChangePasswordScreen(
    theme: ThemeOption,
    onBackClick: () -> Unit = {},
    userViewModel: UserViewModel = hiltViewModel()
) {
    val oldPassword by userViewModel.oldPassword.collectAsState()
    val newPassword by userViewModel.newPassword.collectAsState()
    val updatePasswordState by userViewModel.updatePasswordState.collectAsState()
    val color = theme.toColor()
    val context = LocalContext.current

    var oldPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(updatePasswordState) {
        when (updatePasswordState) {
            is UpdatePasswordState.Error -> {
                val msg = (updatePasswordState as UpdatePasswordState.Error).message
                when {
                    msg?.contains("Old password is incorrect", ignoreCase = true) == true -> {
                        oldPasswordError = "Old password is incorrect"
                    }
                    msg?.contains("closed") == true -> {
                        oldPasswordError = "Old password is incorrect"
                    }
                    else -> {
                        oldPasswordError = "Unknown error, please try again!"
                    }
                }
            }
            UpdatePasswordState.Success -> {
                oldPasswordError = null
                newPasswordError = null
                Toast.makeText(context, "Updated successfully!", Toast.LENGTH_SHORT).show()
                onBackClick()
            }
            else -> {
                oldPasswordError = null
                newPasswordError = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = Color(0xFF1B1C1F).copy(alpha = 0.8f),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onBackClick() }
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Change Password",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        PasswordTextField(
            text = "Old Password",
            placeholder = "Enter your old password",
            password = oldPassword,
            onPasswordChange = {
                userViewModel.setOldPassword(it)
                oldPasswordError = null
            },
            errorText = oldPasswordError
        )

        Spacer(modifier = Modifier.height(24.dp))

        PasswordTextField(
            text = "New Password",
            placeholder = "Enter your new password",
            password = newPassword,
            onPasswordChange = {
                userViewModel.setNewPassword(it)
                newPasswordError = null
            },
            errorText = newPasswordError
        )

        Spacer(modifier = Modifier.weight(1f))

        ButtonSection(
            onClick = {
                oldPasswordError = null
                newPasswordError = null

                if (oldPassword.isBlank()) {
                    oldPasswordError = "This field cannot be empty!"
                } else if (newPassword.isBlank()) {
                    newPasswordError = "This field cannot be empty!"
                } else if (!isValidPassword(newPassword)) {
                    newPasswordError = "Password must be at least 8 characters,\ninclude a digit and an uppercase letter!"
                } else if (!isValidPassword(oldPassword)) {
                    oldPasswordError = "Password must be at least 8 characters,\ninclude a digit and an uppercase letter!"
                } else {
                    userViewModel.updatePassword()
                }
            },
            text = "Save Change",
            colors = ButtonDefaults.buttonColors(containerColor = color)
        )
    }
}

fun isValidPassword(password: String): Boolean {
    val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
    return passwordRegex.matches(password)
}