package com.github.myra.kmongo.data.guild

import com.github.myra.kmongo.ColorHexSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class DbWelcoming(
    val guildId: String,
    val channel: String?,
    val directMessage: DbDirectMessage,
    val embed: DbEmbed,
    val image: DbImage
)

@Serializable
data class DbDirectMessage(
    val toggled: Boolean,
    val message: String?
)

@Serializable
data class DbEmbed(
    val toggled: Boolean,
    val message: String?,
    @Serializable(with = ColorHexSerializer::class) val colour: Color?
)

@Serializable
data class DbImage(
    val toggled: Boolean,
    val image: String?,
    val font: String
)