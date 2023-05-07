package com.example.mystoryappdicoding.util

import android.Manifest.permission.CAMERA

class Const {
    companion object {
        const val TOKEN = "token"
        const val LOGIN = "login_auth"
        const val DETAIL = "detail"
        const val SUCCESS = "upload_success"
        const val CAMERAX_RESULT = 200
        const val REQ_CODE = 23
        const val LIST_LOCATION = "list_location"
        const val LIST_USERNAME = "list_username"

        val REQ_PERMISSION = arrayOf(CAMERA)
        val emailPattern = Regex("[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+")
    }
}