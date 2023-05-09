package com.example.mystoryappdicoding.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryappdicoding.R
import com.example.mystoryappdicoding.data.misc.AuthPreferences
import com.example.mystoryappdicoding.data.repo.AuthRepo
import com.example.mystoryappdicoding.databinding.ActivityRegisterBinding
import com.example.mystoryappdicoding.util.*
import com.example.mystoryappdicoding.util.Const.Companion.emailPattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authPreferences: AuthPreferences
    private lateinit var authRepo: AuthRepo
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        authPreferences = AuthPreferences(this)
        authRepo = AuthRepo()
        authViewModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepo, this)
        ) [AuthViewModel::class.java]

        binding.btnRegister.setSafeOnClickListener { register() }
        binding.btnLogin.setOnClickListener { finish() }

        animation()
    }

    private fun register() {
        val username = binding.edtUsername.text.toString().trim()
        val email = binding.edtEmailInput.text.toString().trim()
        val password = binding.edtPasswordInput.text.toString().trim()

        authViewModel.isLoading.observe(this) { isLoading ->
            showLoading(binding.progressBar, isLoading)
        }

        when {
            username.isEmpty() -> {
                binding.edtUsername.error = "Nama tidak boleh kosong"
            }
            email.isEmpty() -> {
                binding.edtEmailInput.error = "Email tidak boleh kosong"
            }
            password.isEmpty() -> {
                binding.edtPasswordInput.error = "Password tidak boleh kosong"
            }
            password.length < 8 -> {
                binding.edtPasswordInput.error = "Karakter harus lebih dari 8"
            }
            !email.matches(emailPattern) -> {
                binding.edtEmailInput.error = "Format email tidak sesuai"
            }
            else -> {
                authViewModel.register(username, email, password)
                authViewModel.regMessage.observe(this) {
                    it.getContentIfNotHandled()?.let {
                        showToast(this, getString(R.string.email_taken))
                    }
                }
                authViewModel.registerUser.observe(this) { register ->
                    if (register != null) {
                        finish()
                        showToast(this, getString(R.string.registration_success))
                    }
                }
            }
        }
    }

    private fun animation() {
        val title = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(300)
        val registerImage = ObjectAnimator.ofFloat(binding.imgRegisterImage, View.ALPHA, 1f).setDuration(300)
        val name = ObjectAnimator.ofFloat(binding.username, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(name, email, password)
        }
        val togetherSecond = AnimatorSet().apply {
            playTogether(btnLogin, btnRegister)
        }
        AnimatorSet().apply {
            playSequentially(title, registerImage, together, togetherSecond)
            start()
        }
    }
}