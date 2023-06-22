package com.mawumbo.storyapp.model

import com.google.gson.annotations.SerializedName

data class AllStoriesResponse(
    val error: Boolean,
    val message: String,
    @SerializedName("listStory")
    val stories: List<Story>
)