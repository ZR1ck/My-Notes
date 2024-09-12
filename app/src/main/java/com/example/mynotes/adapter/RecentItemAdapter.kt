package com.example.mynotes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.databinding.ItemRecentLayoutBinding

class RecentItemAdapter(private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecentItemAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, value: String)
        fun onRemoveClick(view: View, value: String)
    }

    private val differCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    class MyViewHolder(val binding: ItemRecentLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemRecentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tv.text = differ.currentList[position]
        holder.binding.btnDelete.setOnClickListener {
            onItemClickListener.onRemoveClick(it, differ.currentList[position])
        }
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(it, differ.currentList[position])
        }
    }

}