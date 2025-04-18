package com.example.birthly.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.birthly.createWorkRequest
import com.example.birthly.model.Birthday
import com.example.birthly.repositories.AuthRepository
import com.example.birthly.repositories.BirthdayRepository
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BirthlyViewModel(
    private val authRepository: AuthRepository,
    private val birthdayRepository: BirthdayRepository
) : ViewModel() {

    init {
        fetchBirthdays()
    }

    var userState = mutableStateOf(authRepository.getCurrentUser())
    private val vertexAI = Firebase.vertexAI
    private val generativeModel = vertexAI.generativeModel("gemini-1.5-flash")

    private val _greeting = MutableStateFlow<String?>(null) // StateFlow to hold the greeting
    val greeting: StateFlow<String?> get() = _greeting

    private val _searchText = MutableStateFlow<String?>(null)
    val searchText :StateFlow<String?> get() = _searchText

    private val _isSearching = MutableStateFlow(false)
    val isSearching : StateFlow<Boolean> get() = _isSearching

    private val _birthdaysList = MutableStateFlow<List<Birthday>>(emptyList())
    val birthdaysList: StateFlow<List<Birthday>> get() = _birthdaysList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    val filteredList = birthdaysList
        .combine(searchText) { birthdays, text ->
            if (text.isNullOrEmpty()) birthdays
            else birthdays.filter { it.name.contains(text.trim(), ignoreCase = true) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


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

    private fun getBirthdayPrompt(name:String?, age:Int?, description: String?): String {
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
        return prompt
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

    private fun fetchBirthdays() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = birthdayRepository.getBirthdays()
            result.onSuccess { fetchedBirthdays ->
                _birthdaysList.value = fetchedBirthdays
                _errorMessage.value = null // Clear any previous errors
            }.onFailure { exception ->
                _errorMessage.value = exception.message
            }
        }
    }

    fun onSearchTextChange(text: String){
        _searchText.value = text
    }
}
