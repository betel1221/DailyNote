package com.example.dailynote.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailynote.data.local.AppDatabase
import com.example.dailynote.data.model.Note
import com.example.dailynote.repository.AppRepository
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        val quoteDao = AppDatabase.getDatabase(application).quoteDao()
        repository = AppRepository(noteDao, quoteDao)
    }

    fun saveNote(title: String, content: String, category: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val note = Note(
                title = title,
                content = content,
                category = category,
                isFavorite = isFavorite
            )
            repository.insertNote(note)
        }
    }
}
