package com.example.dailynote.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dailynote.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        binding.btnSaveNote.setOnClickListener {
            val title = binding.etNoteTitle.text.toString()
            val content = binding.etNoteContent.text.toString()
            val category = binding.etNoteCategory.text.toString()
            val isFavorite = binding.cbFavorite.isChecked

            if (title.isNotEmpty() && content.isNotEmpty()) {
                viewModel.saveNote(title, content, category, isFavorite)
                Toast.makeText(requireContext(), "Note Saved!", Toast.LENGTH_SHORT).show()
                // Navigate back or clear fields
                binding.etNoteTitle.text.clear()
                binding.etNoteContent.text.clear()
                binding.etNoteCategory.text.clear()
                binding.cbFavorite.isChecked = false
            } else {
                Toast.makeText(requireContext(), "Title and Content cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
