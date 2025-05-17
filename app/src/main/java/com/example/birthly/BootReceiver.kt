package com.example.birthly

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.birthly.repositories.BirthdayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Boot completed: rescheduling alarms")

            val repository = BirthdayRepository()

            CoroutineScope(Dispatchers.IO).launch {
                val result = repository.getBirthdays()
                result.onSuccess { birthdays ->
                    for (birthday in birthdays) {
                        if (!birthday.notifyTime.isNullOrEmpty()) {
                            scheduleBirthdayReminder(
                                context = context,
                                name = birthday.name,
                                birthdate = birthday.birthdate,
                                notifyTime = birthday.notifyTime
                            )
                        }
                    }
                }.onFailure {
                    Log.e("BootReceiver", "Failed to fetch birthdays on boot: ${it.message}")
                }
            }
        }
    }
}
