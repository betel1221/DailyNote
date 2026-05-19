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

import androidx.lifecycle.switchMap

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository = AppRepository(
        AppDatabase.getDatabase(application).noteDao(),
        AppDatabase.getDatabase(application).quoteDao()
    )
    
    val allNotes: LiveData<List<Note>> = repository.allNotes
    
    private val _dailyQuote = MutableLiveData<Quote?>()
    val dailyQuote: LiveData<Quote?> = _dailyQuote

    private val searchQuery = MutableLiveData<String>("")
    val filteredNotes: LiveData<List<Note>> = searchQuery.switchMap { query ->
        if (query.isNullOrEmpty()) {
            repository.allNotes
        } else {
            repository.searchNotes(query)
        }
    }

    init {
        fetchDailyQuote()
    }

    private fun fetchDailyQuote() {
        viewModelScope.launch {
            val quote = repository.fetchRandomQuote()
            _dailyQuote.postValue(quote)
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
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
}
