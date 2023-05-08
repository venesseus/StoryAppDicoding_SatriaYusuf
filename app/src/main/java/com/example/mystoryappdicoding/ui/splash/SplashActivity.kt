package com.example.mystoryappdicoding.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.mystoryappdicoding.R
import com.example.mystoryappdicoding.data.misc.AuthPreferences
import com.example.mystoryappdicoding.ui.login.LoginActivity
import com.example.mystoryappdicoding.ui.main.MainActivity
import com.example.mystoryappdicoding.util.Token
import com.example.mystoryappdicoding.util.showToast

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var authPreferences: AuthPreferences
    private lateinit var token: Token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authPreferences = AuthPreferences(this)
        token = Token(authPreferences)

        Handler(Looper.getMainLooper()).postDelayed({
            token()
        }, 1500)
    }

    private fun token() {
        token.getToken().observe(this) { key ->
            if (key != null) {
                if (!key.equals("NotFound")) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                showToast(this, getString(R.string.error))
            }
        }
    }
}