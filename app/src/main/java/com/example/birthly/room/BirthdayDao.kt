package com.example.birthly.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.birthly.model.Birthday

@Dao
interface BirthdayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(birthday: Birthday)

    @Query("SELECT * FROM birthdays")
    suspend fun getAll(): List<Birthday>
}