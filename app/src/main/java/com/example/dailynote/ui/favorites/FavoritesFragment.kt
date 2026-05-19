package com.example.dailynote.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dailynote.databinding.FragmentFavoritesBinding
import com.example.dailynote.data.model.Note

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoritesViewModel

    private lateinit var noteAdapter: com.example.dailynote.ui.NoteAdapter
    private lateinit var quoteAdapter: com.example.dailynote.ui.QuoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)

        setupRecyclerViews()
        setupObservers()

        return binding.root
    }

    private fun setupRecyclerViews() {
        noteAdapter = com.example.dailynote.ui.NoteAdapter(
            onItemClick = { note -> showNoteDetailDialog(note) },
            onEditClick = { note ->
                // In Favorites, tapping Edit shows the detail dialog (no nav action available here)
                showNoteDetailDialog(note)
            }
        )
        quoteAdapter = com.example.dailynote.ui.QuoteAdapter()

        binding.recyclerViewFavoriteNotes.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }

        binding.recyclerViewFavoriteQuotes.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = quoteAdapter
        }
    }

    private fun setupObservers() {
        var hasNotes = false
        var hasQuotes = false

        viewModel.favoriteNotes.observe(viewLifecycleOwner) { notes ->
            noteAdapter.setData(notes)
            hasNotes = !notes.isNullOrEmpty()
            updateEmptyState(hasNotes, hasQuotes)
        }

        viewModel.favoriteQuotes.observe(viewLifecycleOwner) { quotes ->
            quoteAdapter.setData(quotes)
            hasQuotes = !quotes.isNullOrEmpty()
            updateEmptyState(hasNotes, hasQuotes)
        }
    }

    private fun updateEmptyState(hasNotes: Boolean, hasQuotes: Boolean) {
        if (!hasNotes && !hasQuotes) {
            binding.layoutFavoritesContent.visibility = View.GONE
            binding.layoutFavoritesEmpty.visibility = View.VISIBLE
        } else {
            binding.layoutFavoritesContent.visibility = View.VISIBLE
            binding.layoutFavoritesEmpty.visibility = View.GONE
            
            // Toggle visibility of headers & lists depending on individual emptiness
            if (hasNotes) {
                binding.tvHeaderNotes.visibility = View.VISIBLE
                binding.recyclerViewFavoriteNotes.visibility = View.VISIBLE
            } else {
                binding.tvHeaderNotes.visibility = View.GONE
                binding.recyclerViewFavoriteNotes.visibility = View.GONE
            }

            if (hasQuotes) {
                binding.tvHeaderQuotes.visibility = View.VISIBLE
                binding.recyclerViewFavoriteQuotes.visibility = View.VISIBLE
            } else {
                binding.tvHeaderQuotes.visibility = View.GONE
                binding.recyclerViewFavoriteQuotes.visibility = View.GONE
            }
        }
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
