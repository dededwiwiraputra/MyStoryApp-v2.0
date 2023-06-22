package com.mawumbo.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.mawumbo.storyapp.data.resource.Resource
import com.mawumbo.storyapp.model.*
import java.io.File

interface StoryRepository {

    fun getAllStory(): LiveData<PagingData<Story>>

    fun getAllStoryWithLocation(): LiveData<List<Story>>

    suspend fun getStory(storyId: String): Resource<DetailStoryResponse>

    suspend fun uploadImage(photo: File, description: String): FileUploadResponse

    suspend fun register(body: RegisterBody): RegisterResponse

    suspend fun login(body: LoginBody): LoginResponse

    suspend fun logout()

}