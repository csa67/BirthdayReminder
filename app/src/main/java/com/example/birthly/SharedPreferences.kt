package com.example.birthly

import android.content.Context
import androidx.core.content.edit

private const val PREFS_NAME = "app_prefs"
private const val KEY_FIRST_LAUNCH = "first_launch_done"

// Check if it's the first launch
fun isFirstLaunch(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return !prefs.getBoolean(KEY_FIRST_LAUNCH, false)  // false means first launch
}

// Mark that first launch is done
fun markFirstLaunchDone(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit() { putBoolean(KEY_FIRST_LAUNCH, true) }
}
