package com.example.dailynote.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailynote.R
import com.example.dailynote.data.model.Quote

class QuoteAdapter : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    private var quotes = emptyList<Quote>()

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.tv_item_quote_content)
        val author: TextView = itemView.findViewById(R.id.tv_item_quote_author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quote, parent, false)
        return QuoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val currentQuote = quotes[position]
        holder.content.text = "\"${currentQuote.content}\""
        holder.author.text = "- ${currentQuote.author}"
    }

    override fun getItemCount(): Int {
        return quotes.size
    }

    fun setData(newQuotes: List<Quote>) {
        this.quotes = newQuotes
        notifyDataSetChanged()
    }
}
