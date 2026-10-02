package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TaskEntity::class,
        DailyReflectionEntity::class,
        WeeklyReviewEntity::class,
        ArcStrategyEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class WinterArcDatabase : RoomDatabase() {
    abstract fun winterArcDao(): WinterArcDao

    companion object {
        @Volatile
        private var INSTANCE: WinterArcDatabase? = null

        fun getDatabase(context: Context): WinterArcDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WinterArcDatabase::class.java,
                    "winter_arc_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
