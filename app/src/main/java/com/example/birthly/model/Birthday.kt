package com.example.birthly.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="birthdays")
data class Birthday(
    @PrimaryKey
    val name: String = "",
    val birthdate: String = "",
    val notifyTime: String? = null
)
