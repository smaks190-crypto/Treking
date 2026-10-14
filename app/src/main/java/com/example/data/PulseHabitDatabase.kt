package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HabitEntity::class], version = 1, exportSchema = false)
abstract class PulseHabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: PulseHabitDatabase? = null

        fun getDatabase(context: Context): PulseHabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PulseHabitDatabase::class.java,
                    "pulse_habit.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
