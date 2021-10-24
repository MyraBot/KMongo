package com.github.myra.kmongo.data.guild

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.m5rian.kotlingua.Kotlingua
import com.github.m5rian.kotlingua.Lang
import com.github.myra.kmongo.cache.impl.guild.*
import com.github.myraBot.diskord.common.entities.Guild
import kotlinx.serialization.Serializable
import org.litote.kmongo.pull
import org.litote.kmongo.push

@Suppress("ArrayInDataClass")
@Serializable
data class DbGuild(
        val guildId: String,
        val prefixes: MutableList<String>,
        @JsonProperty("language")
        private val _language: String,
        val premium: Boolean,
        val unicorn: String?,
        val reactionRoles: MutableList<DbReactionRole>,
        val autoRoles: MutableList<String>,
        val logChannel: String?,
    // val commands???
) {
    val language: Lang get() = Kotlingua.getLanguageByIso(_language)!!
}

suspend fun Guild.prefixes(): MutableList<String> = CacheGuild.load(this.id).prefixes
suspend fun Guild.addPrefix(prefix: String) = CacheGuild.update(this.id, { it.prefixes.add(prefix) }, push(DbGuild::prefixes, prefix))
suspend fun Guild.removePrefix(prefix: String) = CacheGuild.update(this.id, { it.prefixes.remove(prefix) }, pull(DbGuild::prefixes, prefix))
suspend fun Guild.language(): Lang = CacheGuild.load(this.id).language

suspend fun Guild.leveling(): DbLeveling = CacheGuildLeveling.load(this.id)
suspend fun Guild.economy(): DbEconomy = CacheGuildEconomy.load(this.id)
suspend fun Guild.suggestions(): DbSuggestions = CacheGuildSuggestions.load(this.id)
suspend fun Guild.youtube(): DbYoutube = CacheGuildNotificationsYoutube.load(this.id)
suspend fun Guild.twitch(): DbTwitch = CacheGuildNotificationsTwitch.load(this.id)
