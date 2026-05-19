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

    fun searchNotes(query: String): LiveData<List<Note>> = noteDao.searchNotes(query)
    fun getAllNotesSortedByDateAsc(): LiveData<List<Note>> = noteDao.getAllNotesSortedByDateAsc()
    fun getAllNotesSortedByTitleAsc(): LiveData<List<Note>> = noteDao.getAllNotesSortedByTitleAsc()
    fun getAllNotesSortedByTitleDesc(): LiveData<List<Note>> = noteDao.getAllNotesSortedByTitleDesc()
    fun searchNotesByDateAsc(query: String): LiveData<List<Note>> = noteDao.searchNotesByDateAsc(query)
    fun searchNotesByTitleAsc(query: String): LiveData<List<Note>> = noteDao.searchNotesByTitleAsc(query)
    fun searchNotesByTitleDesc(query: String): LiveData<List<Note>> = noteDao.searchNotesByTitleDesc(query)

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
                getRandomFallbackQuote()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getRandomFallbackQuote()
        }
    }

    private fun getRandomFallbackQuote(): Quote {
        val fallbacks = listOf(
            Quote("fb1", "The only way to do great work is to love what you do.", "Steve Jobs"),
            Quote("fb2", "Believe you can and you're halfway there.", "Theodore Roosevelt"),
            Quote("fb3", "Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
            Quote("fb4", "It always seems impossible until it's done.", "Nelson Mandela"),
            Quote("fb5", "Don't watch the clock; do what it does. Keep going.", "Sam Levenson"),
            Quote("fb6", "You miss 100% of the shots you don't take.", "Wayne Gretzky"),
            Quote("fb7", "The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt")
        )
        return fallbacks.random()
    }
}
