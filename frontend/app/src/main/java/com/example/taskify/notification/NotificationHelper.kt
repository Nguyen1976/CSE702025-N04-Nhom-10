package com.example.taskify.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object NotificationHelper {

    const val TASK_CHANNEL_ID = "task_channel"
    const val TASK_CHANNEL_NAME = "Task Reminders"

    fun createTaskNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TASK_CHANNEL_ID,
                TASK_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies before task deadlines"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun scheduleTaskReminder(
        context: Context,
        taskId: String,
        taskTitle: String,
        taskDate: String,
        taskTime: String
    ) {
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime = LocalDateTime.parse("$taskDate $taskTime", formatter)
            val deadlineMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val notifyTime = deadlineMillis - 60 * 60 * 1000 // 1 tiếng trước

            if (notifyTime <= System.currentTimeMillis()) {
                return
            }

            val intent = Intent(context, TaskReminderReceiver::class.java).apply {
                putExtra("taskId", taskId)
                putExtra("taskTitle", taskTitle)
            }

            val requestCode = (taskId + taskDate + taskTime).hashCode()

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(AlarmManager::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notifyTime,
                        pendingIntent
                    )
                } else { }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notifyTime,
                    pendingIntent
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}