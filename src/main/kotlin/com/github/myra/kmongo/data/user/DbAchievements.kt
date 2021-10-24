package com.github.myra.kmongo.data.user

import kotlinx.serialization.Serializable

@Serializable
data class DbAchievements(
    val inviteMyra: Boolean = false
)
