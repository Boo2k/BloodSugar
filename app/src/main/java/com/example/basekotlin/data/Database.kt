package com.example.basekotlin.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.basekotlin.data.history.History
import com.example.basekotlin.data.history.HistoryDao


@androidx.room.Database(entities = [History::class], version = 3)
@TypeConverters(Convert::class)
abstract class Database : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        private var instance: Database? = null

        fun getInstance(context: Context?): Database {
            return instance ?: synchronized(this) {
                val room = context?.let {
                    Room.databaseBuilder(it.applicationContext, Database::class.java, "Database")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
                instance = room
                return instance as Database
            }
        }
    }
}