package com.example.dailynote.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dailynote.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.dailyQuote.observe(viewLifecycleOwner) { quote ->
            if (quote != null) {
                binding.tvQuoteContent.text = "\"${quote.content}\""
                binding.tvQuoteAuthor.text = "- ${quote.author}"
                binding.btnFavoriteQuote.setOnClickListener {
                    viewModel.saveFavoriteQuote(quote)
                }
            } else {
                binding.tvQuoteContent.text = "Failed to load quote"
            }
        }
    }

    private fun setupListeners() {
        // Implement Search and Recycler View later
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
