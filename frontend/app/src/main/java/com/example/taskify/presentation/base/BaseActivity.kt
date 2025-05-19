package com.example.taskify.presentation.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

        // Bắt đầu đếm thời gian token hết hạn
        startSessionTimer()

        // Lắng nghe sự kiện token hết hạn từ interceptor (nếu có)
        TokenExpirationHandler.addListener {
            runOnUiThread {
                if (!isDialogShown) {
                    showSessionExpiredDialog()
                }
            }
        }
    }

    fun startSessionTimer() {
        sessionJob?.cancel()

        // gọi suspend function trong coroutine
        sessionJob = lifecycleScope.launch {
            val delayMillis = getTokenExpiryDuration()
            if (delayMillis <= 0) {
                showSessionExpiredDialog()
                return@launch
            }
            delay(delayMillis)
            if (!isDialogShown) {
                showSessionExpiredDialog()
            }
        }
    }

    fun resetSessionTimer() {
        startSessionTimer()
    }

    private suspend fun getTokenExpiryDuration(): Long {
        val token = tokenManager.getAccessToken() ?: return 0L

        return try {
            val parts = token.split(".")
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

    private fun showSessionExpiredDialog() {
        if (isDialogShown) return

        isDialogShown = true
        sessionExpiredDialog = AlertDialog.Builder(this)
            .setTitle("Phiên đăng nhập hết hạn")
            .setMessage("Phiên đăng nhập của bạn đã hết hạn. Vui lòng đăng nhập lại.")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                lifecycleScope.launch {
                    tokenManager.clearTokens()
                    goToLogin()
                }
            }
            .create()
        sessionExpiredDialog?.show()
    }

    private fun goToLogin() {
        val intent = Intent(this, DashboardActivity::class.java) // Thay AuthActivity bằng Activity đăng nhập của bạn
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