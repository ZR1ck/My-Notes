package com.example.mynotes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.OnItemClickListener
import com.example.mynotes.databinding.ItemLayoutBinding
import com.example.mynotes.model.Note

class NoteItemAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<NoteItemAdapter.MyViewHolder>() {

    var differ = AsyncListDiffer(this, DifferCallback())

    class MyViewHolder(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    private class DifferCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentNote = differ.currentList[position]
        holder.binding.tvBody.text = currentNote.body
        holder.binding.tvTitle.text = currentNote.title
        holder.binding.tvDate.text = currentNote.lastUpdate
        if (currentNote.favourite) {
            holder.binding.favourite.visibility = View.VISIBLE
        }
        else holder.binding.favourite.visibility = View.GONE

        holder.itemView.setOnClickListener {
            listener.onItemClick(it, currentNote)
        }
    }


}