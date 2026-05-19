package com.example.dailynote.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailynote.R
import com.example.dailynote.data.model.Note

class NoteAdapter(private val onItemClick: (Note) -> Unit) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notes = emptyList<Note>()

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_item_note_title)
        val category: TextView = itemView.findViewById(R.id.tv_item_note_category)
        val content: TextView = itemView.findViewById(R.id.tv_item_note_content)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.iv_item_note_favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notes[position]
        holder.title.text = currentNote.title
        holder.category.text = currentNote.category
        holder.content.text = currentNote.content
        
        if (currentNote.isFavorite) {
            holder.favoriteIcon.visibility = View.VISIBLE
        } else {
            holder.favoriteIcon.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(currentNote)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun setData(newNotes: List<Note>) {
        this.notes = newNotes
        notifyDataSetChanged()
    }
}
