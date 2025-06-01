package com.darekbx.emailbot.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.emailbot.repository.database.dao.SpamDao
import com.darekbx.emailbot.repository.database.entities.SpamDto

@Database(entities = [SpamDto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun spamDao(): SpamDao

    companion object {
        const val DB_NAME = "bot_database"
    }
}