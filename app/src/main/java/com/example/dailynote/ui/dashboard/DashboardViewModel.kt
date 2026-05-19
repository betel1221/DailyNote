package com.example.dailynote.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailynote.data.local.AppDatabase
import com.example.dailynote.data.model.Note
import com.example.dailynote.data.model.Quote
import com.example.dailynote.repository.AppRepository
import kotlinx.coroutines.launch

enum class SortOrder { DATE_DESC, DATE_ASC, TITLE_ASC, TITLE_DESC }

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository = AppRepository(
        AppDatabase.getDatabase(application).noteDao(),
        AppDatabase.getDatabase(application).quoteDao()
    )

    val allNotes: LiveData<List<Note>> = repository.allNotes

    private val _dailyQuote = MutableLiveData<Quote?>()
    val dailyQuote: LiveData<Quote?> = _dailyQuote

    private val searchQuery = MutableLiveData<String>("")
    private val sortOrder = MutableLiveData<SortOrder>(SortOrder.DATE_DESC)

    // Combine search + sort reactively via MediatorLiveData
    val filteredNotes: MediatorLiveData<List<Note>> = MediatorLiveData()

    private var currentSource: LiveData<List<Note>>? = null

    init {
        fetchDailyQuote()
        // React to either search or sort changes
        filteredNotes.addSource(searchQuery) { refreshNotes() }
        filteredNotes.addSource(sortOrder) { refreshNotes() }
    }

    private fun refreshNotes() {
        val query = searchQuery.value ?: ""
        val order = sortOrder.value ?: SortOrder.DATE_DESC

        // Remove old source
        currentSource?.let { filteredNotes.removeSource(it) }

        val newSource: LiveData<List<Note>> = when {
            query.isBlank() -> when (order) {
                SortOrder.DATE_DESC  -> repository.allNotes
                SortOrder.DATE_ASC   -> repository.getAllNotesSortedByDateAsc()
                SortOrder.TITLE_ASC  -> repository.getAllNotesSortedByTitleAsc()
                SortOrder.TITLE_DESC -> repository.getAllNotesSortedByTitleDesc()
            }
            else -> when (order) {
                SortOrder.DATE_DESC  -> repository.searchNotes(query)
                SortOrder.DATE_ASC   -> repository.searchNotesByDateAsc(query)
                SortOrder.TITLE_ASC  -> repository.searchNotesByTitleAsc(query)
                SortOrder.TITLE_DESC -> repository.searchNotesByTitleDesc(query)
            }
        }

        currentSource = newSource
        filteredNotes.addSource(newSource) { filteredNotes.value = it }
    }

    fun setSearchQuery(query: String) {
        if (searchQuery.value != query) searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        if (sortOrder.value != order) sortOrder.value = order
    }

    fun getCurrentSortOrder(): SortOrder = sortOrder.value ?: SortOrder.DATE_DESC

    private fun fetchDailyQuote() {
        viewModelScope.launch {
            val quote = repository.fetchRandomQuote()
            _dailyQuote.postValue(quote)
        }
    }

    fun saveFavoriteQuote(quote: Quote) {
        viewModelScope.launch {
            repository.insertFavoriteQuote(quote.copy(isFavorite = true))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { repository.updateNote(note) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { repository.deleteNote(note) }
    }
}
