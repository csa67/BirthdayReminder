package com.example.birthly.ui.theme

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import androidx.work.WorkRequest
import com.example.birthly.NotificationWorker
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
fun createWorkRequestWithDelay(name: String, delayMillis: Long): WorkRequest {
    val inputData = Data.Builder()
        .putString("name", name)
        .putString("birthdate", "11/28/2024")
        .putString("notifyTime","22:55")// Dummy value for testing
        .build()

    return OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()
}

@RequiresApi(Build.VERSION_CODES.O)
fun scheduleTestNotification(context: Context) {
    val workRequest = createWorkRequestWithDelay("Test User", 60000) // 1 minute = 60,000 ms
    WorkManager.getInstance(context).enqueue(workRequest)
}


