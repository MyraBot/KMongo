package com.github.myra.kmongo.data.guild

import kotlinx.serialization.Serializable

@Serializable
data class DbReactionRole(
    val role: String,
    val message: String,
    val emoji: String,
    val type: String
)