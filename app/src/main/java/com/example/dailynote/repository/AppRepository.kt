package com.example.dailynote.repository

import androidx.lifecycle.LiveData
import com.example.dailynote.data.local.NoteDao
import com.example.dailynote.data.local.QuoteDao
import com.example.dailynote.data.model.Note
import com.example.dailynote.data.model.Quote
import com.example.dailynote.data.remote.RetrofitClient

class AppRepository(
    private val noteDao: NoteDao,
    private val quoteDao: QuoteDao
) {
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()
    val favoriteNotes: LiveData<List<Note>> = noteDao.getFavoriteNotes()
    val favoriteQuotes: LiveData<List<Quote>> = quoteDao.getFavoriteQuotes()

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    fun searchNotes(query: String): LiveData<List<Note>> {
        return noteDao.searchNotes(query)
    }

    suspend fun insertFavoriteQuote(quote: Quote) {
        quoteDao.insertQuote(quote)
    }

    suspend fun deleteFavoriteQuote(quote: Quote) {
        quoteDao.deleteQuote(quote)
    }

    suspend fun fetchRandomQuote(): Quote? {
        return try {
            val response = RetrofitClient.api.getRandomQuote()
            if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
