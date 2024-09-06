package com.example.mynotes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.mynotes.model.Note

@Dao
interface NoteDAO {
    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    //Room automatically handles background threading for LiveData queries so don't need to use suspend.
    @Query("Select * from Note")
    fun getAll() : LiveData<List<Note>>

    @RawQuery(observedEntities = [Note::class])
    fun sortBy(query: SupportSQLiteQuery) : LiveData<List<Note>>

    @Query("Select * from Note where title like :query or body like :query")
    fun search(query: String) : LiveData<List<Note>>
}