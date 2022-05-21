package com.dicoding.picodiploma.storyApp.view.story

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.AdapterStoryBinding

class AdapterStory: PagingDataAdapter<ListStory, AdapterStory.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    class ViewHolder(private val binding : AdapterStoryBinding, private val onItemClickCallback: OnItemClickCallback) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storyItem: ListStory) = with(binding){
            tvName.text = storyItem.name

            Glide.with(itemView.context)
                .load(storyItem.photoUrl)
                .centerCrop()
                .into(ivPreview)

            itemView.setOnClickListener {
                onItemClickCallback.onItemClick(storyItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AdapterStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view, onItemClickCallback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    interface OnItemClickCallback{
        fun onItemClick(result: ListStory)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStory> = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldStory: ListStory, newStory: ListStory): Boolean {
                return oldStory.photoUrl == newStory.photoUrl
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldStory: ListStory, newStory: ListStory): Boolean {
                return oldStory == newStory
            }
        }
    }

}


