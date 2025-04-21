package com.example.imc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.imc.dao.ImcDao
import com.example.imc.model.ImcEntity

@Database(entities = [ImcEntity::class], version = 1)
abstract class ImcDatabase : RoomDatabase() {

    abstract fun imcDao(): ImcDao

    companion object {
        @Volatile
        private var INSTANCE: ImcDatabase? = null

        fun getDatabase(context: Context): ImcDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImcDatabase::class.java,
                    "imc_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
