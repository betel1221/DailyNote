package com.example.dailynote.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailynote.data.local.AppDatabase
import com.example.dailynote.data.model.Note
import com.example.dailynote.repository.AppRepository
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository = AppRepository(
        AppDatabase.getDatabase(application).noteDao(),
        AppDatabase.getDatabase(application).quoteDao()
    )

    fun saveNote(title: String, content: String, category: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.insertNote(Note(
                title = title,
                content = content,
                category = category,
                isFavorite = isFavorite
            ))
        }
    }

    fun updateNote(id: Int, title: String, content: String, category: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateNote(Note(
                id = id,
                title = title,
                content = content,
                category = category,
                isFavorite = isFavorite
            ))
        }
    }
}
