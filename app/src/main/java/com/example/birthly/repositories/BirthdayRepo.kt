package com.example.birthly.repositories

import android.util.Log
import com.example.birthly.model.Birthday
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BirthdayRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Adds a birthday to Firestore.
     */
    suspend fun addBirthday(birthday: Birthday): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

        val birthdaysRef = db.collection("users").document(userId).collection("birthdays")

        return try {
            birthdaysRef.add(birthday).await() // Await Firestore operation
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("Adding Birthday error","")
            Result.failure(e)
        }
    }

    /**
     * Retrieves all birthdays from Firestore.
     */
    suspend fun getBirthdays(): Result<List<Birthday>> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

        val birthdaysRef = db.collection("users").document(userId).collection("birthdays")

        return try {
            val snapshot = birthdaysRef.get().await() // Await Firestore operation
            val birthdays = snapshot.documents.mapNotNull { it.toObject(Birthday::class.java) }
            Result.success(birthdays)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
