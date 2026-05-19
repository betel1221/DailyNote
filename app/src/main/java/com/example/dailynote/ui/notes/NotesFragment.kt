package com.example.dailynote.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dailynote.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotesViewModel

    // Edit mode state
    private var editNoteId: Int = -1
    private var isEditMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        // Check if launched in edit mode via Bundle args
        arguments?.let { args ->
            editNoteId = args.getInt("note_id", -1)
            if (editNoteId != -1) {
                isEditMode = true
                binding.etNoteTitle.setText(args.getString("note_title", ""))
                binding.etNoteContent.setText(args.getString("note_content", ""))
                binding.etNoteCategory.setText(args.getString("note_category", ""))
                binding.cbFavorite.isChecked = args.getBoolean("note_is_favorite", false)
                binding.btnSaveNote.text = "Update Note"
            }
        }

        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.btnSaveNote.setOnClickListener {
            val title      = binding.etNoteTitle.text.toString().trim()
            val content    = binding.etNoteContent.text.toString().trim()
            val category   = binding.etNoteCategory.text.toString().trim()
            val isFavorite = binding.cbFavorite.isChecked

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "Title and Content cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditMode) {
                viewModel.updateNote(editNoteId, title, content, category, isFavorite)
                Toast.makeText(requireContext(), "Note updated!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveNote(title, content, category, isFavorite)
                Toast.makeText(requireContext(), "Note saved!", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
