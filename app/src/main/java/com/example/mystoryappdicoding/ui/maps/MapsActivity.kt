package com.example.mystoryappdicoding.ui.maps

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryappdicoding.R
import com.example.mystoryappdicoding.data.misc.AuthPreferences
import com.example.mystoryappdicoding.data.repo.AuthRepo
import com.example.mystoryappdicoding.data.repo.MapsRepo
import com.example.mystoryappdicoding.databinding.ActivityMapsBinding
import com.example.mystoryappdicoding.util.AuthViewModel
import com.example.mystoryappdicoding.util.Const.Companion.LIST_LOCATION
import com.example.mystoryappdicoding.util.Const.Companion.LIST_USERNAME
import com.example.mystoryappdicoding.util.PreferencesFactory
import com.example.mystoryappdicoding.util.Token
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authPreferences: AuthPreferences
    private lateinit var authRepo: AuthRepo
    private lateinit var mapsRepo: MapsRepo
    private lateinit var token: Token
    private lateinit var mMap: GoogleMap

    private val mapViewModel: MapsViewModel by viewModels {
        PreferencesFactory(authPreferences, authRepo, this)
    }

    private var listLocation: ArrayList<LatLng>? = null
    private var listUserName: ArrayList<String>? = null

    private val reqPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getMyLocation()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.location)

        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-1.348994, 115.6112)))

        listLocation = intent.getParcelableArrayListExtra(LIST_LOCATION)
        listUserName = intent.getStringArrayListExtra(LIST_USERNAME)

        mapsModel()
        setMapStyle()
        getMyLocation()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed")
            }
        } catch (exc: NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exc)
        }
    }

    private fun mapsModel() {
        authPreferences = AuthPreferences(this)
        authRepo = AuthRepo()
        mapsRepo = MapsRepo()
        token = Token(authPreferences)

        authViewModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepo, this)
        )[AuthViewModel::class.java]
        token.getToken().observe(this) { token ->
            if (token != null) {
                mapViewModel.getStoryLocation("Bearer $token")
                mapViewModel.getStory().observe(this) { stories ->
                    stories?.let { story ->
                        for (i in story.listIterator()) {
                            val latLng = LatLng(i.lat!!, i.lon!!)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(i.name)
                                    .snippet(i.description)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            reqPermission.launch(ACCESS_FINE_LOCATION)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val TAG = "MapsActivity"
    }
}