package com.example.dailynote.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dailynote.databinding.FragmentDashboardBinding
import com.example.dailynote.data.model.Note
import com.example.dailynote.ui.NoteAdapter

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel

    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter { note ->
            showNoteDetailDialog(note)
        }
        binding.recyclerViewNotes.apply {
            layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL)
            adapter = noteAdapter
        }
    }

    private fun setupObservers() {
        // Observe daily quote
        viewModel.dailyQuote.observe(viewLifecycleOwner) { quote ->
            if (quote != null) {
                binding.tvQuoteContent.text = "\"${quote.content}\""
                binding.tvQuoteAuthor.text = "- ${quote.author}"
                
                // Set favorite icon depending on favorite status
                if (quote.isFavorite) {
                    binding.btnFavoriteQuote.setImageResource(com.example.dailynote.R.drawable.ic_star_filled)
                } else {
                    binding.btnFavoriteQuote.setImageResource(com.example.dailynote.R.drawable.ic_star_outline)
                }

                binding.btnFavoriteQuote.setOnClickListener {
                    viewModel.saveFavoriteQuote(quote)
                    binding.btnFavoriteQuote.setImageResource(com.example.dailynote.R.drawable.ic_star_filled)
                    android.widget.Toast.makeText(requireContext(), "Quote added to Favorites!", android.widget.Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.tvQuoteContent.text = "Failed to load quote"
            }
        }

        // Observe filtered notes
        viewModel.filteredNotes.observe(viewLifecycleOwner) { notes ->
            noteAdapter.setData(notes)
            if (notes.isNullOrEmpty()) {
                binding.recyclerViewNotes.visibility = View.GONE
                binding.layoutEmptyState.visibility = View.VISIBLE
            } else {
                binding.recyclerViewNotes.visibility = View.VISIBLE
                binding.layoutEmptyState.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun showNoteDetailDialog(note: Note) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(com.example.dailynote.R.layout.dialog_note_detail, null)
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        val tvTitle = dialogView.findViewById<android.widget.TextView>(com.example.dailynote.R.id.tv_dialog_title)
        val tvCategory = dialogView.findViewById<android.widget.TextView>(com.example.dailynote.R.id.tv_dialog_category)
        val tvContent = dialogView.findViewById<android.widget.TextView>(com.example.dailynote.R.id.tv_dialog_content)
        val btnFavorite = dialogView.findViewById<android.widget.ImageButton>(com.example.dailynote.R.id.btn_dialog_favorite)
        val btnDelete = dialogView.findViewById<android.widget.ImageButton>(com.example.dailynote.R.id.btn_dialog_delete)
        val btnClose = dialogView.findViewById<com.google.android.material.button.MaterialButton>(com.example.dailynote.R.id.btn_dialog_close)

        // Bind data
        tvTitle.text = note.title
        tvCategory.text = note.category
        tvContent.text = note.content

        // Update favorite star
        var isFavorite = note.isFavorite
        if (isFavorite) {
            btnFavorite.setImageResource(com.example.dailynote.R.drawable.ic_star_filled)
        } else {
            btnFavorite.setImageResource(com.example.dailynote.R.drawable.ic_star_outline)
        }

        btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            val updatedNote = note.copy(isFavorite = isFavorite)
            viewModel.updateNote(updatedNote)
            if (isFavorite) {
                btnFavorite.setImageResource(com.example.dailynote.R.drawable.ic_star_filled)
                android.widget.Toast.makeText(requireContext(), "Pinned to Favorites", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                btnFavorite.setImageResource(com.example.dailynote.R.drawable.ic_star_outline)
                android.widget.Toast.makeText(requireContext(), "Removed from Favorites", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteNote(note)
                    android.widget.Toast.makeText(requireContext(), "Note deleted", android.widget.Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
