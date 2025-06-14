package com.example.taskify.components

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.taskify.R

@Composable
fun TopTitle(
    title: String,
    subTitle: String,
    onBackClick: () -> Unit
) {
    val afacadFont = FontFamily(Font(R.font.afacad))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),

            ) {
            val (welcome, icon) = createRefs()
            Text(
                text = title,
                fontSize = 28.sp,
                fontFamily = afacadFont,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .constrainAs(welcome) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Icon(
                Icons.Default.ArrowBackIos,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(18.dp)
                    .constrainAs(icon) {
                        top.linkTo(parent.top, margin = 8.dp)
                        end.linkTo(welcome.start, margin = 56.dp)
                    }
                    .clickable { onBackClick() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subTitle,
            color = Color.Black.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun ButtonSection(
    onClick: () -> Unit,
    text: String,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF24A19C)
    ),
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(52.dp),
        colors = colors,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}

fun Context.showSessionExpiredDialog(
    onConfirm: () -> Unit,
    title: String,
    message: String
): AlertDialog {
    return AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton("OK") { _, _ -> onConfirm() }
        .create()
}