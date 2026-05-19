package com.example.dailynote.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dailynote.data.model.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY timestamp ASC")
    fun getAllNotesSortedByDateAsc(): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY title ASC")
    fun getAllNotesSortedByTitleAsc(): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY title DESC")
    fun getAllNotesSortedByTitleDesc(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%' ORDER BY timestamp DESC")
    fun searchNotes(searchQuery: String): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%' ORDER BY timestamp ASC")
    fun searchNotesByDateAsc(searchQuery: String): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%' ORDER BY title ASC")
    fun searchNotesByTitleAsc(searchQuery: String): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%' ORDER BY title DESC")
    fun searchNotesByTitleDesc(searchQuery: String): LiveData<List<Note>>
}
