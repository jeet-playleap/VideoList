package com.example.videolist

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.videolist.placeholder.PlaceholderContent.PlaceholderItem
import com.example.videolist.databinding.FragmentItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(

) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
    var  values: ArrayList<PlaceholderItem> = ArrayList()
    fun addItem(item: PlaceholderItem){
        values.add(item)
        notifyItemInserted(values.size-1)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content
        Log.e("Test","dskf${item.id} $position")
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        init {
            Log.e("Test","Android$position")
        }

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}