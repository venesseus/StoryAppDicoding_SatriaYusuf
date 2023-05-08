package com.example.mystoryappdicoding.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mystoryappdicoding.R
import com.example.mystoryappdicoding.data.response.ListStory
import com.example.mystoryappdicoding.databinding.ItemRowBinding
import com.example.mystoryappdicoding.ui.detail.DetailActivity
import com.example.mystoryappdicoding.util.Const.Companion.DETAIL
import com.example.mystoryappdicoding.util.setSafeOnClickListener
import com.example.mystoryappdicoding.util.showToast
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class MainAdapter : PagingDataAdapter<ListStory, MainAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(item: ListStory) {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("ID")
            val time = dateFormat.parse(item.createdAt)?.time
            val prettyTime = PrettyTime(Locale.getDefault())
            val date = prettyTime.format(time?.let { Date(it) })

            with(binding) {
                tvName.text = item.name
                tvCreated.text = date
                tvDescription.text = item.description
                Glide.with(itemView)
                    .load(item.photoUrl)
                    .apply(RequestOptions().centerCrop())
                    .into(imgStory)
            }
            itemView.setSafeOnClickListener {
                with(it.context) {
                    val detailIntent = Intent(this, DetailActivity::class.java)
                    detailIntent.putExtra(DETAIL, item)
                    startActivity(detailIntent)
                }
            }
            val isFavorite = MutableLiveData(false)
            binding.favorite.setOnClickListener {
                val context: Context = binding.root.context
                if (isFavorite.value == false) {
                    binding.favorite.setImageResource(R.drawable.baseline_favorite_24)
                    isFavorite.value = true
                    Toast.makeText(context, "Telah difavoritkan", Toast.LENGTH_SHORT).show()
                } else {
                    binding.favorite.setImageResource(R.drawable.baseline_favorite_border_24)
                    isFavorite.value = false
                    Toast.makeText(context, "Telah dilupakan :(", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MainAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.ViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }
        }
        val noopListUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
            }

            override fun onRemoved(position: Int, count: Int) {
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
            }
        }
    }
}