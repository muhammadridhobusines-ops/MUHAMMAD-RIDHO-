package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteCarEntity::class], version = 1, exportSchema = false)
abstract class MrbDatabase : RoomDatabase() {
    abstract fun favoriteCarDao(): FavoriteCarDao

    companion object {
        @Volatile
        private var INSTANCE: MrbDatabase? = null

        fun getDatabase(context: Context): MrbDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MrbDatabase::class.java,
                    "mrb_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
