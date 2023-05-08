package com.example.mystoryappdicoding.data

import com.example.mystoryappdicoding.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object Dummy {

    fun storyDummy(): List<ListStory> {
        val data: MutableList<ListStory> = arrayListOf()

        for (i in 0..200) {
            val list = ListStory(
                "link",
                "5-8-2023",
                "rafflestest",
                "lagi ngetest",
                "$i",
                40.7143528,
                -74.0059731
            )
            data.add(list)
        }
        return data
    }

    val dummyStoryNull = emptyList<ListStory>()

    fun descDummy(): RequestBody {
        val text = "rafflestest"
        return text.toRequestBody()
    }

    fun imageDummy(): MultipartBody.Part {
        val text = "rafflestest"
        return MultipartBody.Part.create(text.toRequestBody())
    }

    fun loginResult(): LoginResponse {
        return LoginResponse(
            LoginResult(
                userId = "user-geIIL3Qju4jJJoC_",
                name = "rafflestest",
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWdlSUlMM1FqdTRqSkpvQ18iLCJpYXQiOjE2ODM1MzI5OTN9.oVSEmQjROfKelT9I7mlkeSrSnb_NLa7nBUa-U0FkDtU"
            ),
            false, "success"
        )
    }

    fun register(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }

    fun uploadStory(): UploadResponse {
        return UploadResponse(
            error = false,
            message = "success"
        )
    }
}