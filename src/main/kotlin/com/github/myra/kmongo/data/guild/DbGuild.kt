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
import org.litote.kmongo.and
import org.litote.kmongo.eq
import java.util.concurrent.TimeUnit

@Suppress("ArrayInDataClass")
@Serializable
data class DbGuild(
        val guildId: String,
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

suspend fun Guild.language(): Lang = CacheDbGuild.load(this.id).language

suspend fun Guild.leveling(): DbLeveling = CacheDbGuildLeveling.load(this.id)
suspend fun Guild.economy(): DbEconomy = CacheDbGuildEconomy.load(this.id)
suspend fun Guild.suggestions(): DbSuggestions = CacheDbGuildSuggestions.load(this.id)
suspend fun Guild.welcoming(): DbWelcoming = CacheDbGuildWelcoming.load(this.id)
suspend fun Guild.youtube(): DbYoutube = CacheDbGuildNotificationsYoutube.load(this.id)
suspend fun Guild.twitch(): DbTwitch = CacheDbGuildNotificationsTwitch.load(this.id)

val Guild.levelLeaderboard: List<Member>
    get() {
        val members = getDatabaseMembers()
        members.sortWith(Comparator.comparing(DbMember::level).reversed())
        val guild = Diskord.getGuild(this.id)
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId) } }
    }
val Guild.voiceTimeLeaderboard: List<Member>
    get() {
        val members = getDatabaseMembers()
        members.sortWith(Comparator.comparing(DbMember::voiceCallTime).reversed())
        val guild = Diskord.getGuild(this.id)
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId) } }
    }
val Guild.balanceLeaderboard: List<Member>
    get() {
        val members = getDatabaseMembers()
        members.sortWith(Comparator.comparing(DbMember::balance).reversed())
        val guild = Diskord.getGuild(this.id)
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId) } }
    }
val Guild.dailyStreakLeaderboard: List<Member>
    get() {
        val members = getDatabaseMembers()
            .filter { System.currentTimeMillis() - it.lastClaim < TimeUnit.HOURS.toMillis(24) * 2 }
            .toMutableList()
        members.sortWith(Comparator.comparing(DbMember::dailyStreak).reversed())
        val guild = Diskord.getGuild(this.id)
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId) } }
    }

private fun Guild.getDatabaseMembers(): MutableList<DbMember> = Mongo.getAs<DbMember>("members")
    .find(and(
        DbMember::guildId eq this.id,
        DbMember::deleteAt eq null
    ))
    .toMutableList()
