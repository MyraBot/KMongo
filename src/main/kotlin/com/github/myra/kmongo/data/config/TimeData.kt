package com.github.myra.kmongo.data.config

import com.fasterxml.jackson.annotation.JsonProperty

@Suppress("unused")
data class TimeData(
        @JsonProperty("youtube refresh")
        val youtubeRefresh: Long,
        @JsonProperty("twitch refresh")
        val twitchRefresh: Long
)
