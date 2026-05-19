package com.example.dailynote.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.dailynote.data.local.AppDatabase
import com.example.dailynote.data.model.Note
import com.example.dailynote.data.model.Quote
import com.example.dailynote.repository.AppRepository

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    val favoriteNotes: LiveData<List<Note>>
    val favoriteQuotes: LiveData<List<Quote>>

    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        val quoteDao = AppDatabase.getDatabase(application).quoteDao()
        repository = AppRepository(noteDao, quoteDao)
        favoriteNotes = repository.favoriteNotes
        favoriteQuotes = repository.favoriteQuotes
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun deleteFavoriteQuote(quote: Quote) {
        viewModelScope.launch {
            repository.deleteFavoriteQuote(quote)
        }
    }
}

