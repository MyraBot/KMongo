@file:Suppress("unused")

package com.github.myra.kmongo.data.guild

import com.github.myra.kmongo.cache.impl.guild.CacheGuildNotificationsTwitch
import com.github.myra.kmongo.cache.impl.guild.CacheGuildNotificationsYoutube
import kotlinx.serialization.Serializable
import org.litote.kmongo.setValue

@Serializable
data class DbYoutube(
        val guildId: String,
        var channel: String?,
        val subscriptions: MutableList<String>,
        var message: String?
) {
    suspend fun setMessage(message: String?) = CacheGuildNotificationsYoutube.update(this.guildId, { it.message = message }, setValue(DbYoutube::message, message))
    suspend fun setChannel(id: String?) = CacheGuildNotificationsYoutube.update(this.guildId, { it.channel = channel }, setValue(DbYoutube::channel, id))
}

@Serializable
data class DbTwitch(
        var guildId: String,
        var channel: String?,
        val subscriptions: MutableList<String>,
        val message: String?
) {
    suspend fun setChannel(id: String?) = CacheGuildNotificationsTwitch.update(this.guildId, { it.channel = channel }, setValue(DbTwitch::channel, id))
}