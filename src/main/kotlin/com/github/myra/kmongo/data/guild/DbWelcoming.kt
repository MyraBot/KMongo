package com.github.myra.kmongo.data.guild

import kotlinx.serialization.Serializable

@Serializable
data class DbWelcoming(
    val channel: String?,
    val directMessage: DbDirectMessage,
    val embed: DbEmbed,
    val image: DbImage
)

@Serializable
data class DbDirectMessage(
    val toggled: Boolean,
    val message: String
)

@Serializable
data class DbEmbed(
    val toggled: Boolean,
    val message: String
)

@Serializable
data class DbImage(
    val toggled: Boolean,
    val image: String?,
    val font: String
)