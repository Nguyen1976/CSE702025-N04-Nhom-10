package com.example.taskify.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    errorText: String? = null
) {
    var isShowPassword by remember { mutableStateOf(true) }

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

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = {
                Text(
                    text = "Enter your password",
                    color = Color(0xFFA9B0C5),
                    fontSize = 16.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF6F7F9),
                unfocusedContainerColor = Color(0xFFF6F7F9),
                focusedIndicatorColor = Color.DarkGray,
                unfocusedIndicatorColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),

            trailingIcon = {
                IconButton(
                    onClick = { isShowPassword = !isShowPassword }
                ) {
                    Icon(
                        if(isShowPassword) Icons.Filled.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color(0xFFA9B0C5)
                    )
                }
            },
            visualTransformation = if(isShowPassword) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true
        )

        if (errorText != null) {
            Text(
                text = errorText,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun AccountTextField(
    text: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    errorText: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFA9B0C5),
                    fontSize = 16.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF6F7F9),
                unfocusedContainerColor = Color(0xFFF6F7F9),
                focusedIndicatorColor = Color.DarkGray,
                unfocusedIndicatorColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        if (errorText != null) {
            Text(
                text = errorText,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}