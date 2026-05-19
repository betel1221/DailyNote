package com.example.dailynote.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dailynote.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)

        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        // Observe favorite notes and favorite quotes here to populate lists
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
