package com.example.taskify.presentation.auth.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.example.taskify.R
import com.example.taskify.data.local.TokenManager
import com.example.taskify.presentation.auth.signIn.SignInActivity
import com.example.taskify.presentation.auth.signUp.SignUpActivity
import com.example.taskify.presentation.main.MainActivity
import com.example.taskify.ui.theme.TaskifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        lifecycleScope.launch {
            val token = tokenManager.getAccessToken()
            if (!token.isNullOrEmpty()) {
                // User đã đăng nhập -> chuyển sang MainActivity luôn
                startActivity(Intent(this@DashboardActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            } else {
                // Nếu chưa đăng nhập thì set UI Compose bình thường
                setContent {
                    DashboardSection()
                }
            }
        }
    }
}

@Composable
fun DashboardSection() {
    val afacadFont = FontFamily(Font(R.font.afacad))
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome to",
                fontSize = 26.sp,
                color = Color.Black,
                fontFamily = afacadFont,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                "TASKIFY",
                fontSize = 26.sp,
                color = Color(0xFF24A19C),
                fontFamily = afacadFont,
                fontWeight = FontWeight.SemiBold
            )
        }

        Image(
            painter = painterResource(R.drawable.login_img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentScale = ContentScale.Crop
        )

        Button(
            onClick = {
                val intent = Intent(context, SignUpActivity::class.java)
                startActivity(context, intent, null)
            },
            modifier = Modifier
                .padding(top = 56.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF24A19C)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.email),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Continue with email",
                    fontSize = 16.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .background(color = Color.LightGray)
                    .weight(1f)
            )

            Text(
                text = "or continue with",
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Box(
                modifier = Modifier
                    .height(1.dp)
                    .background(color = Color.LightGray)
                    .weight(1f)
            )
        }

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF3F5F9)
            )
        ) {
            Image(
                painter = painterResource(R.drawable.goog),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(4.dp))

            Text(
                "Sign in with Google",
                color = Color.Black,
                fontSize = 16.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Already have an account? ",
                color = Color.Black.copy(alpha = 0.6f)
            )
            Text(
                "Sign in",
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.clickable {
                    val intent = Intent(context, SignInActivity::class.java)
                    startActivity(context, intent, null)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    TaskifyTheme {
        DashboardSection()
    }
}