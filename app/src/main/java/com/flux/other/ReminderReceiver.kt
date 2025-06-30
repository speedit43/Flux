package com.flux.other

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.flux.R
import androidx.core.net.toUri
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("TITLE") ?: "Reminder"
        val description = intent.getStringExtra("DESCRIPTION") ?: "It's time to complete pending things"
        val id = intent.getIntExtra("ID", 0)
        val type = intent.getStringExtra("TYPE") ?: "habit"
        val repeat = intent.getStringExtra("REPEAT") ?: "NONE"
        val notificationId = getUniqueRequestCode(type, id)

        val notification = NotificationCompat.Builder(context, "notification_channel")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)

        if (repeat != "NONE") {
            val nextTime = Calendar.getInstance()
            when (repeat) {
                "DAILY" -> nextTime.add(Calendar.DAY_OF_YEAR, 1)
                "WEEKLY" -> nextTime.add(Calendar.WEEK_OF_YEAR, 1)
                "MONTHLY" -> nextTime.add(Calendar.MONTH, 1)
                "YEARLY" -> nextTime.add(Calendar.YEAR, 1)
            }

            scheduleReminder(
                context = context,
                id = id,
                type=type,
                repeat=repeat,
                timeInMillis = nextTime.timeInMillis,
                title = title,
                description = description,
            )
        }
    }
}

fun createNotificationChannel(context: Context) {
    if(!isNotificationPermissionGranted(context)){ requestNotificationPermission(context as Activity) }

    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel("notification_channel", "Reminders", importance)
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(channel)
}

fun requestExactAlarmPermission(context: Context) {
    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
        data = "package:${context.packageName}".toUri()
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        Toast.makeText(context, "Unable to open alarm permission settings", Toast.LENGTH_LONG).show()
    }
}

fun canScheduleHabitReminder(context: Context): Boolean{
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    return alarmManager.canScheduleExactAlarms()
}

fun scheduleReminder(
    context: Context,
    id: Int,
    type: String,
    repeat: String = "NONE",
    timeInMillis: Long,
    title: String,
    description: String
) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TITLE", title)
            putExtra("DESCRIPTION", description)
            putExtra("ID", id)
            putExtra("TYPE", type)
            putExtra("REPEAT", repeat)
        }

        val requestCode = getUniqueRequestCode(type, id)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    } catch (e: SecurityException) {
        e.printStackTrace()
        Toast.makeText(context, "Error: Failed to schedule alarm", Toast.LENGTH_LONG).show()
    }
}

private fun getUniqueRequestCode(type: String, id: Int): Int {
    return when (type) {
        "habit" -> id
        else -> 200_000 + id
    }
}

fun cancelReminder(context: Context, id: Int, type: String) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val requestCode = getUniqueRequestCode(type, id)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    } catch (e: SecurityException) {
        e.printStackTrace()
        Toast.makeText(context, "Error: Failed to cancel alarm", Toast.LENGTH_LONG).show()
    }
}

fun isNotificationPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
}

fun requestNotificationPermission(activity: Activity, requestCode: Int = 1001) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        requestCode
    )
}