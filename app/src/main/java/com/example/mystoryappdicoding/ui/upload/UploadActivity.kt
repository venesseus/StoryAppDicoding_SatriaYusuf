package com.example.mystoryappdicoding.ui.upload

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryappdicoding.R
import com.example.mystoryappdicoding.data.misc.AuthPreferences
import com.example.mystoryappdicoding.data.repo.AuthRepo
import com.example.mystoryappdicoding.data.repo.UploadRepo
import com.example.mystoryappdicoding.databinding.ActivityUploadBinding
import com.example.mystoryappdicoding.ui.camera.CameraActivity
import com.example.mystoryappdicoding.ui.main.MainActivity
import com.example.mystoryappdicoding.util.*
import com.example.mystoryappdicoding.util.Const.Companion.CAMERAX_RESULT
import com.example.mystoryappdicoding.util.Const.Companion.REQ_CODE
import com.example.mystoryappdicoding.util.Const.Companion.REQ_PERMISSION
import com.example.mystoryappdicoding.util.Const.Companion.SUCCESS
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var authPreferences: AuthPreferences
    private lateinit var authRepo: AuthRepo
    private lateinit var uploadRepo: UploadRepo

    private lateinit var token: Token
    private lateinit var authViewModel: AuthViewModel

    private val uploadViewModel: UploadViewModel by viewModels {
        PreferencesFactory(authPreferences, authRepo, this)
    }

    private var location: LatLng? = null
    private var getFile: File? = null

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                location()
            }
        }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CAMERAX_RESULT) {
            @Suppress("DEPRECATION")
            val myFile = result.data?.getSerializableExtra("Picture") as File
            val isBackCamera = result.data?.getBooleanExtra("IsBackCamera", true) as Boolean

            getFile = myFile
            val results = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            results.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(myFile))
            binding.ivStory.setImageBitmap(BitmapFactory.decodeFile(myFile.path))
        }
    }


    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImage, this)
            getFile = myFile
            binding.ivStory.setImageURI(selectedImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_story)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this, REQ_PERMISSION, REQ_CODE
            )
        }

        authPreferences = AuthPreferences(this)
        authRepo = AuthRepo()
        uploadRepo = UploadRepo()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        token = Token(authPreferences)
        authViewModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepo, this)
        )[AuthViewModel::class.java]

        binding.btnGallery.setSafeOnClickListener { gallery() }
        binding.btnCamera.setSafeOnClickListener { cameraX() }
        binding.btnUpload.setSafeOnClickListener { upload() }

        binding.swLocation.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                location()
            }
        }
    }

    private fun location() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) location = LatLng(loc.latitude, loc.longitude)
            }
        } else {
            showToast(this, getString(R.string.allow_location))
            requestPermission.launch(ACCESS_FINE_LOCATION)
        }
    }

    private fun allPermissionGranted() = REQ_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun cameraX() {
        val toCameraIntent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(toCameraIntent)
    }

    private fun gallery() {
        val galleryIntent = Intent()
        galleryIntent.action = ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        val chooser = Intent.createChooser(galleryIntent, getString(R.string.select_image))
        launcherIntentGallery.launch(chooser)
    }

    private fun upload() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val reqImage = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo", file.name, reqImage
            )
            val description =
                binding.edtDesc.text.toString().toRequestBody("text/plain".toMediaType())

            if (binding.edtDesc.text.isEmpty()) {
                binding.edtDesc.error = getString(R.string.description_cannot_be_empty)
            } else {
                token.getToken().observe(this) { token ->
                    if (location != null) {
                        uploadViewModel.uploadStory(
                            "Bearer $token", imageMultiPart, description,
                            lat = (location as LatLng).latitude,
                            lon = (location as LatLng).longitude
                        )
                    } else {
                        uploadViewModel.uploadStory(
                            "Bearer $token",
                            imageMultiPart,
                            description
                        )
                    }
                    uploadViewModel.isEnabled.observe(this) { isEnabled ->
                        binding.btnUpload.isEnabled = isEnabled
                    }
                    uploadViewModel.isLoading.observe(this) { isLoading ->
                        showLoading(binding.progressBar2, isLoading)
                    }
                    uploadViewModel.storyResponse.observe(this) { response ->
                        if (!response.error) {
                            showToast(this, getString(R.string.upload_success))
                            this.getSharedPreferences("data", 0).edit().clear().apply()

                            val mainIntent = Intent(this, MainActivity::class.java)
                            mainIntent.putExtra(SUCCESS, true)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(mainIntent)
                            finish()
                        } else {
                            showToast(this, getString(R.string.upload_failed))
                        }
                    }
                }
            }
        } else showToast(this, getString(R.string.cannot_post))
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_CODE) {
            if (!allPermissionGranted()) {
                showToast(this, getString(R.string.permission))
                finish()
            }
        }
    }
}