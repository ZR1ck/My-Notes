package com.example.mynotes

import android.view.View
import com.example.mynotes.model.Note

interface OnItemClickListener {
    fun onItemClick(view : View, note : Note)
}