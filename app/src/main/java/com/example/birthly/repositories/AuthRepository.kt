package com.example.birthly.repositories

import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    suspend fun signIn(email: String, password: String) : FirebaseUser? {
        return try{
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: FirebaseAuthException ){
            Log.e("AuthRepository","Sign-in failed: ${e.message}")
            null
        }
    }

    suspend fun signUp(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: FirebaseAuthException) {
            Log.e("AuthRepository", "Sign-up failed: ${e.message}")
            null
        }
    }

    // Log out
    fun logOut() {
        auth.signOut()
    }

    // Get Current User
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}