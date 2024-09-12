package com.example.mynotes.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val body: String,
    val createDate: String,
    val lastUpdate: String,
    var favourite: Boolean
) : Parcelable