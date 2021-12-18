package com.github.myra.kmongo.data.guild

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.m5rian.kotlingua.Kotlingua
import com.github.m5rian.kotlingua.Lang
import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.impl.guild.*
import com.github.myra.kmongo.data.member.DbMember
import com.github.myraBot.diskord.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.litote.kmongo.eq
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
suspend fun Guild.welcoming(): DbWelcoming = CacheGuildWelcoming.load(this.id)
suspend fun Guild.youtube(): DbYoutube = CacheGuildNotificationsYoutube.load(this.id)
suspend fun Guild.twitch(): DbTwitch = CacheGuildNotificationsTwitch.load(this.id)

val Guild.levelLeaderboard: List<Member>
    get() {
        val members = Mongo.getAs<DbMember>("members")
            .find(DbMember::guildId eq this.id)
            .toMutableList()
        members.sortWith(Comparator.comparing(DbMember::level).reversed())
        val guild = Diskord.getGuild(this.id)
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId) } }
    }
val Guild.voiceTimeLeaderboard: List<Member>
    get() {
        val members = Mongo.getAs<DbMember>("members")
            .find(DbMember::guildId eq this.id)
            .toMutableList()
        members.sortWith(Comparator.comparing(DbMember::voiceCallTime).reversed())
        val guild = Diskord.getGuild(this.id)
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId) } }
    }
val Guild.balanceLeaderboard: List<Member>
    get() {
        val members = Mongo.getAs<DbMember>("members")
            .find(DbMember::guildId eq this.id)
            .toMutableList()
        members.sortWith(Comparator.comparing(DbMember::balance).reversed())
        val guild = Diskord.getGuild(this.id)
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId) } }
    }
val Guild.dailyStreakLeaderboard: List<Member>
    get() {
        val members = Mongo.getAs<DbMember>("members")
            .find(DbMember::guildId eq this.id)
            .toMutableList()
        members.sortWith(Comparator.comparing(DbMember::dailyStreak).reversed())
        val guild = Diskord.getGuild(this.id)
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId) } }
    }
