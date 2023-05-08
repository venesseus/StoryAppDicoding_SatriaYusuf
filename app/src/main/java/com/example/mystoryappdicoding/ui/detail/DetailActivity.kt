package com.example.mystoryappdicoding.ui.detail

import android.content.Context
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.mystoryappdicoding.R
import com.example.mystoryappdicoding.data.response.ListStory
import com.example.mystoryappdicoding.databinding.ActivityDetailBinding
import com.example.mystoryappdicoding.util.Const.Companion.DETAIL
import com.example.mystoryappdicoding.util.withDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_story)

        @Suppress("DEPRECATION")
        val detail = intent.getParcelableExtra<ListStory>(DETAIL) as ListStory
        val geocoder = Geocoder(this, Locale.getDefault())
        val address = detail.lat?.let { latitude ->
            detail.lon?.let { longitude ->
                geocoder.getFromLocation(latitude, longitude, 1)
            }
        }

        val cityName = address?.get(0)?.subAdminArea
        val stateName = address?.get(0)?.adminArea
        val countryName = address?.get(0)?.countryName
        val addressName = "$cityName, $stateName, $countryName"

        binding.apply {
            tvNameDetail.text = detail.name
            tvDescriptionDetail.text = detail.description
            tvCreatedDetail.text = detail.createdAt.withDateFormat()
            Glide.with(this@DetailActivity)
                .load(detail.photoUrl)
                .transform(CenterInside(), RoundedCorners(25))
                .into(imgStoryDetail)
            if (address != null) {
                tvLocation.text = addressName
            } else {
                tvLocation.isVisible = false
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

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}