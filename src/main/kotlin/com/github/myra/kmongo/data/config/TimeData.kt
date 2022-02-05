package com.github.myra.kmongo.data.config

import kotlinx.serialization.SerialName

@Suppress("unused")
data class TimeData(
    @SerialName("youtube refresh") val youtubeRefresh: Long,
    @SerialName("twitch refresh") val twitchRefresh: Long
)
