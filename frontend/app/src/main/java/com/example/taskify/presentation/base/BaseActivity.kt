package com.example.taskify.presentation.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.taskify.components.showSessionExpiredDialog
import com.example.taskify.data.local.TokenManager
import com.example.taskify.data.network.TokenExpirationHandler
import com.example.taskify.presentation.auth.dashboard.DashboardActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Base64
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    private var isDialogShown = false
    private var sessionExpiredDialog: AlertDialog? = null
    private var sessionJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bắt đầu đếm thời gian hết hạn refresh token
        startSessionTimer()

        // Lắng nghe token hết hạn từ interceptor (ví dụ khi refresh token không hợp lệ)
        TokenExpirationHandler.addListener {
            runOnUiThread {
                if (!isDialogShown) {
                    displaySessionExpiredDialog()
                }
            }
        }
    }

    private fun startSessionTimer() {
        sessionJob?.cancel()

        sessionJob = lifecycleScope.launch {
            val delayMillis = getRefreshTokenExpiryDuration()
            if (delayMillis <= 0) {
                displaySessionExpiredDialog()
                return@launch
            }
            delay(delayMillis)
            if (!isDialogShown) {
                displaySessionExpiredDialog()
            }
        }
    }

    fun resetSessionTimer() {
        startSessionTimer()
    }

    private suspend fun getRefreshTokenExpiryDuration(): Long {
        val refreshToken = tokenManager.getRefreshToken() ?: return 0L

        return try {
            val parts = refreshToken.split(".")
            if (parts.size != 3) return 0L

            val payloadJson = String(Base64.getUrlDecoder().decode(parts[1]))
            val jsonObject = JSONObject(payloadJson)
            val exp = jsonObject.optLong("exp", 0L)
            if (exp == 0L) return 0L

            val currentTimeSeconds = System.currentTimeMillis() / 1000
            val secondsLeft = exp - currentTimeSeconds

            if (secondsLeft > 0) secondsLeft * 1000 else 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    private fun displaySessionExpiredDialog() {
        if (isDialogShown) return

        isDialogShown = true
        sessionExpiredDialog = showSessionExpiredDialog(
            onConfirm = {
                lifecycleScope.launch {
                    tokenManager.clearTokens()
                    goToLogin()
                }
            },
            title = "Session Expired",
            message = "Your session has expired. Please log in again."
        )

        sessionExpiredDialog?.show()
    }

    private fun goToLogin() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (isDialogShown) {
            sessionExpiredDialog?.dismiss()
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionJob?.cancel()
    }
}