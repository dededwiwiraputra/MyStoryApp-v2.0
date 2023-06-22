package com.mawumbo.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.mawumbo.storyapp.data.preferences.LoginSession
import com.mawumbo.storyapp.data.remotemediator.StoryRemoteMediator
import com.mawumbo.storyapp.data.local.database.StoryDatabase
import com.mawumbo.storyapp.data.network.ApiService
import com.mawumbo.storyapp.data.resource.Resource
import com.mawumbo.storyapp.model.*
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StoryRepositoryImpl @Inject constructor(
    private val database: StoryDatabase,
    private val api: ApiService,
    private val session: LoginSession
) : StoryRepository {

    override fun getAllStory(): LiveData<PagingData<Story>> = Pager(
        config = PagingConfig(pageSize = 20, initialLoadSize = 40, enablePlaceholders = true),
        remoteMediator = StoryRemoteMediator(database, api, session),
        pagingSourceFactory = {
            database.storyDao().getAllStories()
        }
    ).liveData

    override fun getAllStoryWithLocation(): LiveData<List<Story>> = liveData {
        val token = "Bearer ${
            session.loginSessionFlow.first {
                it.isNotEmpty()
            }
        }"
        emit(api.getStoriesWithLocation(token).stories)
    }

    override suspend fun getStory(storyId: String): Resource<DetailStoryResponse> {
        val token = "Bearer ${
            session.loginSessionFlow.first {
                it.isNotEmpty()
            }
        }"

        return api.getStoryDetail(storyId, token)
    }

    override suspend fun uploadImage(photo: File, description: String): FileUploadResponse {
        val desc = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = photo.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            requestImageFile
        )

        val token = "Bearer ${
            session.loginSessionFlow.first {
                it.isNotEmpty()
            }
        }"
        val response = try {
            api.uploadImage(imageMultipart, desc, token)
        } catch (e: HttpException) {
            FileUploadResponse(true, e.message())
        }

        return response
    }

    override suspend fun register(body: RegisterBody): RegisterResponse {
        val response = try {
            api.register(body)
        } catch (exception: HttpException) {
            RegisterResponse(true, exception.message())
        }

        return response
    }

    override suspend fun login(body: LoginBody): LoginResponse {
        val response = try {
            api.login(body)
        } catch (exception: HttpException) {
            Log.d("Login", "login: $exception")
            LoginResponse(true, exception.message(), null)
        }

        if (!response.error) {
            response.user?.token?.let { session.updateLoginSession(it) }
        }

        return response
    }

    override suspend fun logout() {
        session.updateLoginSession("")
    }
}