package com.example.basekotlin.all.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.basekotlin.R
import com.example.basekotlin.base.tap
import com.example.basekotlin.data.Note

class NoteAdapter(
    var context: Context,
    private var listNote: ArrayList<Note>?,
    var isEditList: Boolean,
    var onClick: (listNoteActive: ArrayList<Note>) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    var listNoteActive: ArrayList<Note> = ArrayList()

    init {
        listNoteActive = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    @SuppressLint("NewApi", "ResourceType")
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note: Note = listNote!![position]

        holder.tvNote.text = note.name

        if (note.isSelected == true) {
            holder.btnNote.backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.color_E7F4DF)
        } else {
            holder.btnNote.backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.color_E7E7E7)
        }

        holder.itemView.tap {
            if (isEditList) {
                setCheck(note.order!!)
            }
            onClick.invoke(listNoteActive)
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return listNote?.size ?: 0
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnNote: RelativeLayout
        var tvNote: TextView

        init {
            btnNote = itemView.findViewById(R.id.btn_note)
            tvNote = itemView.findViewById(R.id.tv_note)
        }
    }

    private fun setCheck(noteId: Int) {
        for (item in listNote!!) {
            if (noteId == item.order) {
                item.isSelected = item.isSelected != true
            }
        }

        listNoteActive = ArrayList()
        for (items in listNote!!) {
            if (items.isSelected!!) {
                listNoteActive.add(items)
            }
        }

        notifyDataSetChanged()
    }

    fun updateData(listNew: ArrayList<Note>) {
        listNote = ArrayList()
        listNote!!.clear()
        listNote!!.addAll(listNew)
        notifyDataSetChanged()
    }

}