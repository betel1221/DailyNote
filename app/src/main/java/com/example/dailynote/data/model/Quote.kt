package com.example.dailynote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,
    val content: String,
    val author: String,
    var isFavorite: Boolean = false
)
