package com.example.dailynote.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dailynote.R
import com.example.dailynote.data.model.Note
import com.example.dailynote.databinding.FragmentDashboardBinding
import com.example.dailynote.ui.NoteAdapter

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            onItemClick = { note -> showNoteDetailDialog(note) },
            onEditClick = { note -> navigateToEdit(note) }
        )
        binding.recyclerViewNotes.apply {
            layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(
                2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
            )
            adapter = noteAdapter
        }
    }

    private fun setupObservers() {
        // Quote card
        viewModel.dailyQuote.observe(viewLifecycleOwner) { quote ->
            if (quote != null) {
                binding.tvQuoteContent.text = "\"${quote.content}\""
                binding.tvQuoteAuthor.text  = "- ${quote.author}"
                binding.btnFavoriteQuote.setImageResource(
                    if (quote.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                )
                binding.btnFavoriteQuote.setOnClickListener {
                    viewModel.saveFavoriteQuote(quote)
                    binding.btnFavoriteQuote.setImageResource(R.drawable.ic_star_filled)
                    Toast.makeText(requireContext(), "Quote added to Favorites!", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.tvQuoteContent.text = "Stay inspired today ✨"
            }
        }

        // Notes (filtered + sorted)
        viewModel.filteredNotes.observe(viewLifecycleOwner) { notes ->
            noteAdapter.setData(notes ?: emptyList())
            val empty = notes.isNullOrEmpty()
            binding.recyclerViewNotes.visibility = if (empty) View.GONE  else View.VISIBLE
            binding.layoutEmptyState.visibility  = if (empty) View.VISIBLE else View.GONE
        }
    }

    private fun setupListeners() {
        // Search
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // Sort button toggles chip group visibility
        binding.btnSort.setOnClickListener {
            val visible = binding.chipGroupSort.visibility == View.VISIBLE
            binding.chipGroupSort.visibility = if (visible) View.GONE else View.VISIBLE
        }

        // Sort chips
        binding.chipGroupSort.setOnCheckedStateChangeListener { _, checkedIds ->
            val order = when (checkedIds.firstOrNull()) {
                R.id.chip_sort_date_asc   -> SortOrder.DATE_ASC
                R.id.chip_sort_title_asc  -> SortOrder.TITLE_ASC
                R.id.chip_sort_title_desc -> SortOrder.TITLE_DESC
                else                      -> SortOrder.DATE_DESC
            }
            viewModel.setSortOrder(order)
        }

        // FAB → Create Note
        binding.fabAddNote.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_notes)
        }
    }

    // Navigate to Notes screen pre-filled for editing
    private fun navigateToEdit(note: Note) {
        findNavController().navigate(
            R.id.action_dashboard_to_notes,
            bundleOf(
                "note_id"         to note.id,
                "note_title"      to note.title,
                "note_content"    to note.content,
                "note_category"   to note.category,
                "note_is_favorite" to note.isFavorite
            )
        )
    }

    private fun showNoteDetailDialog(note: Note) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_note_detail, null)
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        val tvTitle    = dialogView.findViewById<android.widget.TextView>(R.id.tv_dialog_title)
        val tvCategory = dialogView.findViewById<android.widget.TextView>(R.id.tv_dialog_category)
        val tvContent  = dialogView.findViewById<android.widget.TextView>(R.id.tv_dialog_content)
        val btnFav     = dialogView.findViewById<android.widget.ImageButton>(R.id.btn_dialog_favorite)
        val btnDelete  = dialogView.findViewById<android.widget.ImageButton>(R.id.btn_dialog_delete)
        val btnClose   = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_dialog_close)

        tvTitle.text    = note.title
        tvCategory.text = note.category.ifBlank { "General" }
        tvContent.text  = note.content

        var isFavorite = note.isFavorite
        btnFav.setImageResource(if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline)

        btnFav.setOnClickListener {
            isFavorite = !isFavorite
            viewModel.updateNote(note.copy(isFavorite = isFavorite))
            btnFav.setImageResource(if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline)
            val msg = if (isFavorite) "Pinned to Favorites" else "Removed from Favorites"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete \"${note.title}\"?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteNote(note)
                    Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
