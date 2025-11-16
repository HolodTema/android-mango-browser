package com.terabyte.mangobrowser.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terabyte.mangobrowser.databinding.ListItemHistoryTabBinding
import com.terabyte.mangobrowser.db.HistoryTab
import java.text.SimpleDateFormat
import java.util.Locale

interface HistoryTabItemCallbacks {
    fun onHistoryTabDelete(historyTab: HistoryTab)

    fun onHistoryTabClicked(historyTab: HistoryTab)
}

abstract class HistoryTabHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(historyTab: HistoryTab)

}

class HolderNoDate(
    private val binding: ListItemHistoryTabBinding,
    private val callbacks: HistoryTabItemCallbacks
) : HistoryTabHolder(binding.root) {

    private val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override fun bind(historyTab: HistoryTab) {
        binding.textHistoryTabUrl.text = historyTab.url
        binding.textHistoryTabDate.visibility = View.GONE
        binding.textHistoryTabDateTime.text = dateTimeFormat.format(historyTab.date)

        binding.buttonDeleteHistoryTab.setOnClickListener {
            callbacks.onHistoryTabDelete(historyTab)
        }

        binding.viewHistoryTabBackground.setOnClickListener {
            callbacks.onHistoryTabClicked(historyTab)
        }
    }
}

class HolderWithDate(
    private val binding: ListItemHistoryTabBinding,
    private val callbacks: HistoryTabItemCallbacks
) : HistoryTabHolder(binding.root) {

    private val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun bind(historyTab: HistoryTab) {
        binding.textHistoryTabUrl.text = historyTab.url
        binding.textHistoryTabDate.visibility = View.VISIBLE
        binding.textHistoryTabDate.text = dateFormat.format(historyTab.date)
        binding.textHistoryTabDateTime.text = dateTimeFormat.format(historyTab.date)

        binding.buttonDeleteHistoryTab.setOnClickListener {
            callbacks.onHistoryTabDelete(historyTab)
        }

        binding.viewHistoryTabBackground.setOnClickListener {
            callbacks.onHistoryTabClicked(historyTab)
        }
    }
}


private class HistoryTabDiffUtil : DiffUtil.ItemCallback<HistoryTab>() {
    override fun areContentsTheSame(
        oldItem: HistoryTab,
        newItem: HistoryTab
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: HistoryTab,
        newItem: HistoryTab
    ): Boolean {
        return oldItem.id == newItem.id
    }
}

class HistoryTabAdapter(
    private val inflater: LayoutInflater,
    private val callbacks: HistoryTabItemCallbacks
) : ListAdapter<HistoryTab, HistoryTabHolder>(HistoryTabDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryTabHolder {
        val binding = ListItemHistoryTabBinding.inflate(inflater, parent, false)
        return when(viewType) {
            HOLDER_TYPE_WITH_DATE -> {
                HolderWithDate(binding, callbacks)
            }
            HOLDER_TYPE_NO_DATE -> {
                HolderNoDate(binding, callbacks)
            }
            else -> {
                HolderNoDate(binding, callbacks)
            }
        }
    }

    override fun onBindViewHolder(holder: HistoryTabHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return HOLDER_TYPE_WITH_DATE
        }

        val item = getItem(position)
        val previousItem = getItem(position - 1)
        if (item.equalDayTo(previousItem)) {
            return HOLDER_TYPE_NO_DATE
        }
        return HOLDER_TYPE_WITH_DATE
    }

    companion object {
        const val HOLDER_TYPE_WITH_DATE = 0
        const val HOLDER_TYPE_NO_DATE = 1
    }
}