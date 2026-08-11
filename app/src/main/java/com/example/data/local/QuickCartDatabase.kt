package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CartItemEntity::class,
        UserAddressEntity::class,
        OrderEntity::class,
        SavedItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class QuickCartDatabase : RoomDatabase() {

    abstract fun dao(): QuickCartDao

    companion object {
        @Volatile
        private var INSTANCE: QuickCartDatabase? = null

        fun getDatabase(context: Context): QuickCartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuickCartDatabase::class.java,
                    "quickcart_db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
