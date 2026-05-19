package com.example.dailynote.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailynote.data.local.AppDatabase
import com.example.dailynote.data.model.Note
import com.example.dailynote.data.model.Quote
import com.example.dailynote.repository.AppRepository
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    val allNotes: LiveData<List<Note>>
    
    private val _dailyQuote = MutableLiveData<Quote?>()
    val dailyQuote: LiveData<Quote?> = _dailyQuote

    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        val quoteDao = AppDatabase.getDatabase(application).quoteDao()
        repository = AppRepository(noteDao, quoteDao)
        allNotes = repository.allNotes
        fetchDailyQuote()
    }

    private fun fetchDailyQuote() {
        viewModelScope.launch {
            val quote = repository.fetchRandomQuote()
            _dailyQuote.postValue(quote)
        }
    }

    fun searchNotes(query: String): LiveData<List<Note>> {
        return repository.searchNotes(query)
    }

    fun saveFavoriteQuote(quote: Quote) {
        viewModelScope.launch {
            val favQuote = quote.copy(isFavorite = true)
            repository.insertFavoriteQuote(favQuote)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}
