package com.example.birthly

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Data
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters,
): Worker(context,workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val name = inputData.getString("name") ?: "Reminder"
        val birthdate = inputData.getString("birthdate") ?: ""
        val notifyTime = inputData.getString("notifyTime") ?: ""

        println("Worker executing. Name: $name, Birthdate: $birthdate")

        sendNotification(name)

        scheduleForNextYear(name,birthdate,notifyTime)

        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleForNextYear(name: String, birthdate: String, notifyTime: String) {
        val workRequest = createWorkRequest(name,birthdate, notifyTime)
        WorkManager.getInstance(context).enqueue(workRequest)

    }

    private fun sendNotification(name: String) {
        val channelId = "birthday_reminders"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Birthday Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel)
        }

        println("Inside scheduling function")

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("It's $name's birthday!")
            .setContentText("Send them birthday wishes!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(name.hashCode(), notification)
        println("Notification sent successfully for $name")

    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun createWorkRequest(name: String, birthdate: String, notifyTime: String?): WorkRequest {
    val inputData = Data.Builder()
        .putString("name", name)
        .putString("birthdate", birthdate)
        .putString("notifyTime", notifyTime)
        .build()

    val delayMillis = calculateDelay(birthdate, notifyTime)

    return OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()
}


