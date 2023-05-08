package com.example.mystoryappdicoding.ui.main

import android.content.Intent
import android.location.Geocoder
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.mystoryappdicoding.R
import com.example.mystoryappdicoding.data.misc.AuthPreferences
import com.example.mystoryappdicoding.data.repo.AuthRepo
import com.example.mystoryappdicoding.data.response.ListStory
import com.example.mystoryappdicoding.data.room.StoryDatabase
import com.example.mystoryappdicoding.databinding.ActivityMainBinding
import com.example.mystoryappdicoding.ui.login.LoginActivity
import com.example.mystoryappdicoding.ui.maps.MapsActivity
import com.example.mystoryappdicoding.ui.upload.UploadActivity
import com.example.mystoryappdicoding.util.*
import com.example.mystoryappdicoding.util.Const.Companion.LIST_LOCATION
import com.example.mystoryappdicoding.util.Const.Companion.LIST_USERNAME
import com.example.mystoryappdicoding.util.Const.Companion.TOKEN
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    private var mMediaPlayer: MediaPlayer? = null
    private var isReady: Boolean = false

    private lateinit var binding: ActivityMainBinding
    private lateinit var pagingModel: PagingModel

    private lateinit var authPreferences: AuthPreferences
    private lateinit var authRepo: AuthRepo
    private lateinit var authViewModel: AuthViewModel

    private lateinit var token: Token
    private lateinit var storyDatabase: StoryDatabase
    private lateinit var mainAdapter: MainAdapter

    private val mainViewModel: MainViewModel by viewModels {
        PreferencesFactory(authPreferences, authRepo, this)
    }

    private var listLocation: ArrayList<LatLng>? = null
    private var listUserName: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(true)

        init() //Music

        storyDatabase = StoryDatabase.getInstance(this)
        mainAdapter = MainAdapter()
        authPreferences = AuthPreferences(this)
        authRepo = AuthRepo()
        token = Token(authPreferences)

        authViewModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepo, this)
        )[AuthViewModel::class.java]

        pagingModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepo, this)
        )[PagingModel::class.java]

        token.getToken().observe(this) { token ->
            if (token != null) {
                setAdapter()
                mainViewModel.getStory().observe(this) { story ->
                    mainAdapter.submitData(lifecycle, story)
                    mainAdapter.snapshot().items
                }
                mainViewModel.loading.value = false
            } else {
                Log.e(TOKEN, "invalid token")
                finishAffinity()
            }
        }

        mainViewModel.loading.observe(this) { isLoading ->
            showLoading(binding.progressBar, isLoading)
        }

        binding.refresh.setOnRefreshListener { refresh() }
        binding.fabUpload.setSafeOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    private fun setAdapter() {
        val layout = LinearLayoutManager(this@MainActivity)
        val itemDecoration = DividerItemDecoration(this@MainActivity, layout.orientation)

        mainAdapter = MainAdapter()
        mainAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.rvStory.smoothScrollToPosition(0)
                }
            }
        })
        binding.rvStory.apply {
            layoutManager = layout
            smoothScrollToPosition(0)
            addItemDecoration(itemDecoration)
            adapter =
                mainAdapter.withLoadStateFooter(footer = LoadingAdapter { mainAdapter.retry() })
        }
    }

    private fun refresh() {
        binding.refresh.isRefreshing = true
        Timer().schedule(1000) {
            binding.refresh.isRefreshing = false
        }
        binding.rvStory.smoothScrollBy(0, 0)
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.confirmation))
                builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    this.getSharedPreferences("data", 0)
                        .edit().clear().apply()
                    val loginIntent = Intent(this, LoginActivity::class.java)
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .also {
                            token.delToken()
                            startActivity(it)
                        }
                    finishAffinity()
                }
                val alert = builder.create()
                alert.show()
            }
            R.id.location -> {
                startActivity(Intent(this, MapsActivity::class.java)
                    .also {
                        it.putExtra(LIST_LOCATION, listLocation)
                        it.putExtra(LIST_USERNAME, listUserName)
                    }
                )
            }
            R.id.music -> {
                if (!isReady) {
                    mMediaPlayer?.prepareAsync()
                } else {
                    if (mMediaPlayer?.isPlaying as Boolean) {
                        mMediaPlayer?.pause()
                    } else {
                        mMediaPlayer?.start()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        mMediaPlayer = MediaPlayer()
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mMediaPlayer?.setAudioAttributes(attribute)
        val afd = applicationContext.resources.openRawResourceFd(R.raw.suriap)
        try {
            mMediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaPlayer?.setOnPreparedListener {
            isReady = true
            mMediaPlayer?.start()
        }
        mMediaPlayer?.setOnErrorListener { _, _, _ -> false }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        finishAffinity()
    }
}