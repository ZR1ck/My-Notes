package com.example.mynotes.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.mynotes.model.Note
import com.example.mynotes.repository.Repository
import kotlinx.coroutines.launch

class NoteViewModel(app: Application, private val repository: Repository) : AndroidViewModel(app) {

    var type = MutableLiveData(0)
    var asc = MutableLiveData(false)

    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun getAllNotes() : LiveData<List<Note>> {
        return repository.getAll()
    }

    fun get(type: Int = 0, asc: Boolean = false) : LiveData<List<Note>> {
        return repository.get(type, asc)
    }

    fun search(query: String) : LiveData<List<Note>> {
        return repository.search(query)
    }

}