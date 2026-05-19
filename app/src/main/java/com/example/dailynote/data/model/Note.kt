package com.example.dailynote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val category: String,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
