package com.darekbx.emailbot.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.emailbot.repository.database.entities.SpamDto

@Dao
interface SpamDao {

    @Insert
    suspend fun insert(spamDto: SpamDto)

    @Query("SELECT * FROM spam_item")
    suspend fun getAll(): List<SpamDto>
}
