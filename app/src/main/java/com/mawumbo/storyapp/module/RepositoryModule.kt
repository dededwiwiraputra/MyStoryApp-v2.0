package com.mawumbo.storyapp.module

import com.mawumbo.storyapp.data.local.database.StoryDatabase
import com.mawumbo.storyapp.data.network.ApiService
import com.mawumbo.storyapp.data.preferences.LoginSession
import com.mawumbo.storyapp.data.repository.StoryRepository
import com.mawumbo.storyapp.data.repository.StoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideStoryRepository(
        database: StoryDatabase,
        api: ApiService,
        session: LoginSession
    ): StoryRepository = StoryRepositoryImpl(database, api, session)

}