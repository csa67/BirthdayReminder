package com.example.birthly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.birthly.repositories.AuthRepository
import com.example.birthly.repositories.BirthdayRepository

class BirthlyViewModelFactory(
    private val authRepo: AuthRepository,
    private val bdayRepo: BirthdayRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BirthlyViewModel(authRepo, bdayRepo) as T
    }
}