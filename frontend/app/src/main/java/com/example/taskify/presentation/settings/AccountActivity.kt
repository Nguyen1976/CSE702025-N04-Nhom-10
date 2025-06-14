package com.example.taskify.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.components.AccountTextField
import com.example.taskify.components.ButtonSection
import com.example.taskify.data.themeStorage.ThemeDataStore
import com.example.taskify.data.viewmodel.UpdateUsernameState
import com.example.taskify.data.viewmodel.UserViewModel
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.presentation.main.toColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val themeFlow = ThemeDataStore.getSavedTheme(context)
            val theme by themeFlow.collectAsState(initial = ThemeOption.Teal)

            AccountScreen(
                onBackClick = { finish() },
                theme = theme ?: ThemeOption.Teal
            )
        }
    }
}

@Composable
fun AccountScreen(
    onBackClick: () -> Unit,
    theme: ThemeOption,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val username by userViewModel.username.collectAsState()
    var fullNameError by remember { mutableStateOf<String?>(null) }
    val updateUserState by userViewModel.updateUserState.collectAsState()

    val color = theme.toColor()
    val context = LocalContext.current

    LaunchedEffect(updateUserState) {
        when (updateUserState) {
            is UpdateUsernameState.Success -> {
                Toast.makeText(context, "Updated successfully!", Toast.LENGTH_SHORT).show()
                onBackClick()
            }
            is UpdateUsernameState.Error -> {
                Toast.makeText(context, "Update failed, please try again!", Toast.LENGTH_SHORT).show()
            }
            else -> {}
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
            modifier = Modifier
                .fillMaxWidth(),
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
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Account",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        AccountTextField(
            text = "Full Name",
            placeholder = "Full name",
            value = username,
            onValueChange = { userViewModel.setUsername(it) },
            errorText = fullNameError
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Password",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, PasswordActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Change Password",
                    fontSize = 18.sp,
                    color = Color(0xFF767E8C)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        ButtonSection(
            onClick = {
                fullNameError = null
                if (username.isBlank()) {
                    fullNameError = "This field cannot be empty!"
                } else {
                    userViewModel.updateUsername()
                }
            },
            text = "Save Change",
            colors = ButtonDefaults.buttonColors(containerColor = color)
        )
    }
}