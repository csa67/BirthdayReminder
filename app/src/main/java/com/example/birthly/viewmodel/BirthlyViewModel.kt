package com.example.birthly

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.birthly.model.Birthday
import com.example.birthly.repositories.AuthRepository
import com.example.birthly.repositories.BirthdayRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val birthdayRepository: BirthdayRepository = BirthdayRepository()
) : ViewModel() {

    var userState = mutableStateOf<FirebaseUser?>(authRepository.getCurrentUser())

    private val _greeting = MutableStateFlow<String?>(null) // StateFlow to hold the greeting
    val greeting: StateFlow<String?> get() = _greeting

    private val vertexAI = Firebase.vertexAI
    val generativeModel = vertexAI.generativeModel("gemini-1.5-flash")

    // Sign-in
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            userState.value = authRepository.signIn(email, password)
        }
    }

    // Sign-up
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            userState.value = authRepository.signUp(email, password)
        }}

    // Log out
    fun logOut() {
        authRepository.logOut()
        userState.value = null
    }

    fun generateGreeting(name:String, age:Int?, description: String?) {
        val prompt = getBirthdayPrompt(name, age, description)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val contentBuilder = StringBuilder()
                generativeModel.generateContentStream(prompt).collect { response ->
                    contentBuilder.append(response.text)
                }
                _greeting.value = contentBuilder.toString() // Update StateFlow
            } catch (e: Exception) {
                Log.e("BirthdayViewModel", "Error generating greeting", e)
                _greeting.value = null
            }
        }
    }

    fun getBirthdayPrompt(name:String?, age:Int?, description: String?): String {
        val prompt = when {
            !name.isNullOrEmpty() && !description.isNullOrEmpty() && age != null && age > 0 -> {
                "Write a heartfelt birthday message for $name, who is turning $age. Include the following details to make the message special: $description. Keep the tone cheerful, warm, and personal."
            }
            !name.isNullOrEmpty() && age != null && age > 0 -> {
                "Write a warm and cheerful birthday message for $name, who is turning $age. Keep the tone celebratory and uplifting."
            }
            !name.isNullOrEmpty() && !description.isNullOrEmpty() -> {
                "Write a thoughtful birthday message for $name. Include the following details to make the message special: $description. Avoid referencing age. Keep the tone warm and engaging."
            }
            age != null && age > 0 && !description.isNullOrEmpty() -> {
                "Write a universal birthday message for someone turning $age. Include these details to make the message special: $description. Use a friendly tone, and avoid directly addressing their name."
            }
            !name.isNullOrEmpty() -> {
                "Write a cheerful birthday message for $name. Keep the tone warm and celebratory, and avoid referencing age or other details."
            }
            else -> {
                "Write a universal birthday message for someone without specific details. Keep the tone warm, joyful, and suitable for any recipient."
            }
        }
        return prompt;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addBirthday(name: String, birthDate: String, notifyTime: String?, context: Context) {
        val birthday = Birthday(name, birthDate, notifyTime)

        viewModelScope.launch {
            val result =birthdayRepository.addBirthday(birthday)
            if (result.isSuccess) {
                Toast.makeText(context, "Birthday added successfully!", Toast.LENGTH_SHORT).show()
                val workRequest = createWorkRequest(name, birthDate, notifyTime)
                WorkManager.getInstance(context).enqueue(workRequest)
            } else {
                Toast.makeText(context, "Failed to add birthday: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
