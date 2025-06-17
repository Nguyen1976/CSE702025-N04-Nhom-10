package com.example.taskify.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.taskify.R

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("taskTitle") ?: "Task Reminder"
        val taskId = intent.getStringExtra("taskId") ?: taskTitle

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        val notification = NotificationCompat.Builder(context, NotificationHelper.TASK_CHANNEL_ID)
            .setSmallIcon(R.drawable.task_icon)
            .setContentTitle("Task Reminder")
            .setContentText("Your task \"$taskTitle\" is due in 1 hour")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(taskId.hashCode(), notification)
    }
}