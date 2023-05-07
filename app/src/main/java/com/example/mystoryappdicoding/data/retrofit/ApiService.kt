package com.example.mystoryappdicoding.data.retrofit

import com.example.mystoryappdicoding.data.response.LoginResponse
import com.example.mystoryappdicoding.data.response.RegisterResponse
import com.example.mystoryappdicoding.data.response.StoryResponse
import com.example.mystoryappdicoding.data.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): StoryResponse

    @GET("stories")
    fun getStoriesLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStoryLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): Call<UploadResponse>
}