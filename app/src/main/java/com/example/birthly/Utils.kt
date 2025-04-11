package com.example.birthly

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
fun calculateDelay(birthdate: String, notifyTime: String?): Long {
    if (birthdate.isEmpty() || notifyTime.isNullOrEmpty()) {
        println("Invalid input: Birthdate or notifyTime is empty.")
        return 0L
    }

    val (month, day) = birthdate.split("/").map { it.toInt() }
    val today = LocalDate.now()
    val thisYearBirthday = LocalDate.of(today.year, month, day)

    // Parse notifyTime to hour and minute
    val (hour, minute) = notifyTime.split(":").map { it.toInt() }
    val now = LocalDate.now().atTime(LocalTime.now())
    val notifyDateTime = thisYearBirthday.atTime(hour, minute)

    // Case 1: Today is the birthday, and notifyTime is in the future
    if (thisYearBirthday.isEqual(today) && notifyDateTime.isAfter(now)) {
        return notifyDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis()
    }

    // Case 2: NotifyTime has already passed for today or it’s not today
    val targetDate = if (thisYearBirthday.isBefore(today) ||
        (thisYearBirthday.isEqual(today) && notifyDateTime.isBefore(now))) {
        // Schedule for next year
        thisYearBirthday.plusYears(1).atTime(hour, minute)
    } else {
        // Schedule for this year
        thisYearBirthday.atTime(hour, minute)
    }

    // Calculate the delay in milliseconds
    return targetDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis()
}