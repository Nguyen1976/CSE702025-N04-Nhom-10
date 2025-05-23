package com.example.taskify.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.R
import com.example.taskify.data.viewmodel.UserViewModel
import com.example.taskify.domain.model.signInModel.User

@Composable
fun SettingScreen(userViewModel: UserViewModel = hiltViewModel()) {
    val user by userViewModel.userState.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F5))
            .padding(20.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConstraintLayout{
                val (avatar, editBtn) = createRefs()

                Image(
                    painter = painterResource(R.drawable.person),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .constrainAs(avatar) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            clip = false
                        )
                        .background(color = Color.White, CircleShape)
                        .constrainAs(editBtn) {
                            top.linkTo(avatar.bottom, margin = -28.dp)
                            start.linkTo(avatar.end, margin = -28.dp)
                        },
                    contentAlignment = Alignment.Center
                ){
                    Image(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = null,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (user != null) {
                val currentUser = user!!

                Text(
                    text = currentUser.username,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Text(
                    text = currentUser.email,
                    fontSize = 15.sp,
                    color = Color(0xFF767E8C)
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(painter = painterResource(id = R.drawable.ic_account), "Account", onClick = {})
        SettingItem(painter = painterResource(id = R.drawable.ic_theme), "Theme", onClick = {})
        SettingItem(painter = painterResource(id = R.drawable.ic_app_icon), "App icon", onClick = {})
        SettingItem(painter = painterResource(id = R.drawable.ic_productivity), "Productivity", onClick = {})

        Box(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Color.LightGray)
        )

        SettingItem(painter = painterResource(id = R.drawable.ic_key), "Privacy Policy", onClick = {})
        SettingItem(painter = painterResource(id = R.drawable.ic_help_center), "Help Center", onClick = {})
        SettingItem(painter = painterResource(id = R.drawable.ic_logout), "Log Out", onClick = {})
    }
}

@Composable
fun SettingItem(
    painter: Painter,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter,
            contentDescription = null,
            tint = Color(0xFF767E8C),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            color = Color(0xFF767E8C)
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF767E8C),
            modifier = Modifier
                .size(18.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() }
        )
    }
}