package com.terabyte.mangobrowser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terabyte.mangobrowser.databinding.ListItemFavoriteTabBinding
import com.terabyte.mangobrowser.db.FavoriteTab

interface FavoriteTabItemCallbacks {
    fun onFavoriteTabDelete(favoriteTab: FavoriteTab)

    fun onFavoriteTabClicked(favoriteTab: FavoriteTab)
}

class FavoriteTabHolder(
    val binding: ListItemFavoriteTabBinding,
    private val callbacks: FavoriteTabItemCallbacks
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(favoriteTab: FavoriteTab) {
        binding.textFavoriteTabName.text = favoriteTab.name
        binding.textFavoriteTabUrl.text = favoriteTab.url

        binding.buttonDelete.setOnClickListener {
            callbacks.onFavoriteTabDelete(favoriteTab)
        }

        binding.viewFavoriteTabFrame.setOnClickListener {
            callbacks.onFavoriteTabClicked(favoriteTab)
        }
    }
}

private class FavoriteTabDiffUtil : DiffUtil.ItemCallback<FavoriteTab>() {
    override fun areContentsTheSame(
        oldItem: FavoriteTab,
        newItem: FavoriteTab
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: FavoriteTab,
        newItem: FavoriteTab
    ): Boolean {
        return oldItem.id == newItem.id
    }
}

class FavoriteTabAdapter(
    private val inflater: LayoutInflater,
    private val callbacks: FavoriteTabItemCallbacks
) : ListAdapter<FavoriteTab, FavoriteTabHolder>(FavoriteTabDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteTabHolder {
        val binding = ListItemFavoriteTabBinding.inflate(inflater, parent, false)
        return FavoriteTabHolder(binding, callbacks)
    }

    override fun onBindViewHolder(holder: FavoriteTabHolder, position: Int) {
        holder.bind(getItem(position))
    }
}