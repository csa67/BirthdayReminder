package com.example.birthly


import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun calculateNextTriggerTime(birthdate: String, notifyTime: String): Long {
    val formatter = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm")
    val birthdayDateTime = LocalDateTime.parse("$birthdate $notifyTime", formatter)

    val now = LocalDateTime.now()
    var nextBirthday = birthdayDateTime.withYear(now.year)

    if (now > nextBirthday) {
        nextBirthday = nextBirthday.plusYears(1)
    }

    return nextBirthday.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
