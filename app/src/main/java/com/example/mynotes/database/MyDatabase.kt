package com.example.mynotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mynotes.model.Note

@Database(entities = [Note::class], version = 1)
abstract class MyDatabase : RoomDatabase() {

    abstract fun getNoteDAO() : NoteDAO

    // Static attributes
    companion object {
        @Volatile private var instance: MyDatabase ?= null
        private val LOCK = Any()

        operator fun invoke(context: Context) : MyDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, MyDatabase::class.java, "my_database").build()
            }
            return instance as MyDatabase
        }
    }

}