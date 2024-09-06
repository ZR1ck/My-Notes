package com.example.mynotes.repository

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.mynotes.database.MyDatabase
import com.example.mynotes.model.Note

class Repository(private val database: MyDatabase) {

    suspend fun insert(note: Note) {
        database.getNoteDAO().insert(note)
    }

    suspend fun update(note: Note) {
        database.getNoteDAO().update(note)
    }

    suspend fun deleteNote(note: Note) {
        database.getNoteDAO().delete(note)
    }

    fun getAll() : LiveData<List<Note>> {
        return database.getNoteDAO().getAll()
    }

    fun sortBy(column: String, asc: Boolean) : LiveData<List<Note>> {
        val order = if (asc) "ASC" else "DESC"
        val query = SimpleSQLiteQuery("Select * from Note order by $order")
        return database.getNoteDAO().sortBy(query)
    }

    fun search(query: String) : LiveData<List<Note>> {
        return database.getNoteDAO().search(query)
    }
}