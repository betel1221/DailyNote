package com.example.dailynote.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailynote.R
import com.example.dailynote.data.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val onItemClick: (Note) -> Unit,
    private val onEditClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notes = emptyList<Note>()

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView       = itemView.findViewById(R.id.tv_item_note_title)
        val category: TextView    = itemView.findViewById(R.id.tv_item_note_category)
        val content: TextView     = itemView.findViewById(R.id.tv_item_note_content)
        val timestamp: TextView   = itemView.findViewById(R.id.tv_item_note_timestamp)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.iv_item_note_favorite)
        val editBtn: ImageButton  = itemView.findViewById(R.id.btn_item_note_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.title.text    = note.title
        holder.category.text = if (note.category.isBlank()) "General" else note.category
        holder.content.text  = note.content

        val sdf = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
        holder.timestamp.text = sdf.format(Date(note.timestamp))

        holder.favoriteIcon.visibility = if (note.isFavorite) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener { onItemClick(note) }
        holder.editBtn.setOnClickListener  { onEditClick(note) }
    }

    override fun getItemCount() = notes.size

    fun setData(newNotes: List<Note>) {
        this.notes = newNotes
        notifyDataSetChanged()
    }
}
