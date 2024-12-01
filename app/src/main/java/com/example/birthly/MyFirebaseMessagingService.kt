package com.example.birthly

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM message
        val data = remoteMessage.data
        val action = data["action"] // e.g., "update", "delete"
        val name = data["name"]
        val birthdate = data["birthdate"]
        val notifyTime = data["notifyTime"]

        if (action != null && name != null && birthdate != null) {
            when (action) {
                "update" -> {
                    // Reschedule the reminder
                    if (notifyTime != null) {
                        scheduleBirthdayReminder(applicationContext, name, birthdate, notifyTime)
                    }
                }
                "delete" -> {
                    // Cancel the reminder if needed
                    if (notifyTime != null) {
                        cancelBirthdayReminder(applicationContext, name, birthdate)
                    }
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("New FCM Token: $token")

        // Send the new token to your app server
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val firestore = FirebaseFirestore.getInstance()

        // Create a map for the data
        val tokenData = hashMapOf(
            "fcmToken" to token,
            "lastUpdated" to System.currentTimeMillis()
        )

        // Save the token to the user's document in Firestore
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .set(tokenData)
                .addOnSuccessListener {
                    println("FCM Token saved successfully for user $userId")
                }
                .addOnFailureListener { e ->
                    println("Error saving FCM Token: ${e.message}")
                }
        }

        println("Token sent to server: $token")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleBirthdayReminder(context: Context, name: String, birthdate: String, notifyTime: String) {
        val workRequest = createWorkRequest(name, birthdate, notifyTime)
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun cancelBirthdayReminder(context: Context, name: String, birthdate: String) {
        val workManager = WorkManager.getInstance(context)
        val workId = "${name}_$birthdate".hashCode().toString()

        workManager.cancelAllWorkByTag(workId)
    }


}


