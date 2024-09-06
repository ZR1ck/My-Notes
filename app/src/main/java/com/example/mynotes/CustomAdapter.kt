package com.example.mynotes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.databinding.ItemLayoutBinding
import com.example.mynotes.model.Note

class CustomAdapter(private val data: List<Note>) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentNote = data[position]
        holder.binding.tvBody.text = currentNote.body
        holder.binding.tvTitle.text = currentNote.title
        holder.binding.tvDate.text = currentNote.lastUpdate
    }


}