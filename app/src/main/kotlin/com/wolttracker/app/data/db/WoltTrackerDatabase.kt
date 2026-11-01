package com.wolttracker.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wolttracker.app.data.dao.ExpenseDao
import com.wolttracker.app.data.dao.ShiftDao
import com.wolttracker.app.data.model.ExpenseEntity
import com.wolttracker.app.data.model.ShiftEntity

@Database(
    entities = [ShiftEntity::class, ExpenseEntity::class],
    version = 2,
    exportSchema = true
)
abstract class WoltTrackerDatabase : RoomDatabase() {
    abstract fun shiftDao(): ShiftDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: WoltTrackerDatabase? = null

        fun getDatabase(context: Context): WoltTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WoltTrackerDatabase::class.java,
                    "wolt_tracker.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
