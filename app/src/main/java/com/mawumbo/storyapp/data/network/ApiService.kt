package com.mawumbo.storyapp.data.network

import com.mawumbo.storyapp.data.resource.Resource
import com.mawumbo.storyapp.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): AllStoriesResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") id: String, @Header("Authorization") token: String
    ): Resource<DetailStoryResponse>

    @POST("login")
    suspend fun login(@Body body: LoginBody): LoginResponse

    @POST("register")
    suspend fun register(@Body body: RegisterBody): RegisterResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): FileUploadResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): AllStoriesResponse

}