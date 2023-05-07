package com.example.mystoryappdicoding.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryappdicoding.R
import com.example.mystoryappdicoding.data.misc.AuthPreferences
import com.example.mystoryappdicoding.data.repo.AuthRepo
import com.example.mystoryappdicoding.databinding.ActivityLoginBinding
import com.example.mystoryappdicoding.ui.main.MainActivity
import com.example.mystoryappdicoding.ui.register.RegisterActivity
import com.example.mystoryappdicoding.util.*
import com.example.mystoryappdicoding.util.Const.Companion.emailPattern

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authPreferences: AuthPreferences
    private lateinit var authViewModel: AuthViewModel
    private lateinit var token: Token
    private lateinit var authRepo: AuthRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        authPreferences = AuthPreferences(this)
        token = Token(authPreferences)
        authRepo = AuthRepo()
        authViewModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepo, this)
        ) [AuthViewModel::class.java]

        binding.btnLogin.setSafeOnClickListener { login() }
        binding.btnRegister.setSafeOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        animation()
    }

    private fun login() {
        val email = binding.edtEmailInput.text.toString().trim()
        val password = binding.edtPasswordInput.text.toString().trim()
        authViewModel.isEnabled.observe(this) { isEnabled ->
            binding.btnLogin.isEnabled = isEnabled
        }
        authViewModel.isLoading.observe(this) { isLoading ->
            showLoading(binding.progressBar, isLoading)
        }
        when {
            password.isBlank() or email.isBlank() -> {
                showToast(this, getString(R.string.fill_password))
            }
            !email.matches(emailPattern) -> {
                showToast(this, getString(R.string.format_email))
            }
            else -> {
                authViewModel.login(email, password)
                authViewModel.logMessage.observe(this) {
                    it.getContentIfNotHandled()?.let {
                        showToast(this, getString(R.string.login_not_matched))
                    }
                }
                authViewModel.loginUser.observe(this) { login ->
                    token.setToken(login.token)
                    startActivity(Intent(this, MainActivity::class.java))
                    showToast(this, "${getString(R.string.login_success)} ${login.name}")
                }
            }
        }
    }

    private fun animation() {
        val title = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(300)
        val loginImage = ObjectAnimator.ofFloat(binding.imgLoginImage, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(email, password)
        }
        val togetherSecond = AnimatorSet().apply {
            playTogether(btnLogin, btnRegister)
        }
        AnimatorSet().apply {
            playSequentially(title, loginImage, together, togetherSecond)
            start()
        }
    }

}