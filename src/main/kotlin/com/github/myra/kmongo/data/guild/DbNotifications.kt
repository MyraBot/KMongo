@file:Suppress("unused")

package com.github.myra.kmongo.data.guild

import com.github.myra.kmongo.cache.dbTwitch
import com.github.myra.kmongo.cache.dbYoutube
import kotlinx.serialization.Serializable
import org.litote.kmongo.setValue

@Serializable
data class DbYoutube(
    val guildId: String,
    var channel: String?,
    val subscriptions: MutableList<String>,
    var message: String?
) {
    suspend fun setMessage(message: String?) = dbYoutube.update(this.guildId, { it.message = message }, setValue(DbYoutube::message, message))
    suspend fun setChannel(id: String?) = dbYoutube.update(this.guildId, { it.channel = channel }, setValue(DbYoutube::channel, id))
}

@Serializable
data class DbTwitch(
    var guildId: String,
    var channel: String?,
    val subscriptions: MutableList<String>,
    val message: String?
) {
    suspend fun setChannel(id: String?) = dbTwitch.update(this.guildId, { it.channel = channel }, setValue(DbTwitch::channel, id))
}