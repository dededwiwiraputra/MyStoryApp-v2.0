package com.mawumbo.storyapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mawumbo.storyapp.model.Story
import com.mawumbo.storyapp.model.StoryRemoteKey

@Database(
    entities = [
        Story::class,
        StoryRemoteKey::class
    ],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun storyRemoteKeyDao(): StoryRemoteKeyDao

    companion object {
        private const val DATABASE_NAME = "story-db"

        private var instance: StoryDatabase? = null

        fun getInstance(context: Context): StoryDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, StoryDatabase::class.java, DATABASE_NAME)
                    .build().also { instance = it }
            }
        }
    }
}