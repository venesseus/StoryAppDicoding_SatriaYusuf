package com.example.mystoryappdicoding.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mystoryappdicoding.databinding.ItemLoadingBinding
import com.example.mystoryappdicoding.util.setSafeOnClickListener

class LoadingAdapter(private var retry: () -> Unit) :
    LoadStateAdapter<LoadingAdapter.LoadingViewHolder>() {

    inner class LoadingViewHolder(private val binding: ItemLoadingBinding, retry: (() -> Unit)) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.tvErrorConnection.text = loadState.error.localizedMessage
            }
            binding.tvErrorConnection.isVisible = loadState is LoadState.Error
            binding.btnRetry.isVisible = loadState is LoadState.Error
            binding.progressBar.isVisible = loadState is LoadState.Loading
        }

        init {
            binding.btnRetry.setSafeOnClickListener { retry.invoke() }
        }
    }

    override fun onBindViewHolder(holder: LoadingAdapter.LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): LoadingAdapter.LoadingViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, retry)
    }
}