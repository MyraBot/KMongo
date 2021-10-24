package com.github.myra.kmongo.data.user

import kotlinx.serialization.Serializable

@Serializable
data class DbSocials(
        val userId: String,
        val youtube: String?
)
