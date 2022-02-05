package com.github.myra.kmongo.data.guild

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.*
import com.github.myra.kmongo.data.member.DbMember
import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.toList
import org.litote.kmongo.eq
import java.util.concurrent.TimeUnit

@Suppress("ArrayInDataClass")
@Serializable
data class DbGuild(
    val guildId: String,
    val premium: Boolean,
    val unicorn: String?,
    val reactionRoles: MutableList<DbReactionRole>,
    val autoRoles: MutableList<String>,
    val logChannel: String?,
    // val commands???
)

suspend fun Guild.leveling(): DbLeveling = dbLeveling.load(this.id)
suspend fun Guild.economy(): DbEconomy = dbEconomy.load(this.id)
suspend fun Guild.suggestions(): DbSuggestions = dbSuggestions.load(this.id)
suspend fun Guild.welcoming(): DbWelcoming = dbWelcoming.load(this.id)
suspend fun Guild.youtube(): DbYoutube = dbYoutube.load(this.id)
suspend fun Guild.twitch(): DbTwitch = dbTwitch.load(this.id)

val Guild.levelLeaderboard: List<Member>
    get() {
        val members = runBlocking { getDatabaseMembers() }
        members.sortedWith(Comparator.comparing(DbMember::level).reversed())
        val guild = runBlocking { Diskord.getGuild(this@levelLeaderboard.id).await() }
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId)?.awaitNonNull() } }
    }
val Guild.voiceTimeLeaderboard: List<Member>
    get() {
        val members = runBlocking { getDatabaseMembers() }
        members.sortedWith(Comparator.comparing(DbMember::voiceCallTime).reversed())
        val guild = runBlocking { Diskord.getGuild(this@voiceTimeLeaderboard.id).await() }
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId)?.awaitNonNull() } }
    }
val Guild.balanceLeaderboard: List<Member>
    get() {
        val members = runBlocking { getDatabaseMembers() }
        members.sortedWith(Comparator.comparing(DbMember::balance).reversed())
        val guild = runBlocking { Diskord.getGuild(this@balanceLeaderboard.id).await() }
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId)?.awaitNonNull() } }
    }
val Guild.dailyStreakLeaderboard: List<Member>
    get() {
        val members = runBlocking { getDatabaseMembers() }
            .filter { System.currentTimeMillis() - it.lastClaim < TimeUnit.HOURS.toMillis(24) * 2 }
            .toMutableList()
        members.sortWith(Comparator.comparing(DbMember::dailyStreak).reversed())
        val guild = runBlocking { Diskord.getGuild(this@dailyStreakLeaderboard.id).await() }
        return members.mapNotNull { runBlocking { guild?.getMember(it.userId)?.awaitNonNull() } }
    }

private suspend fun Guild.getDatabaseMembers(): List<DbMember> = Mongo.getAs<DbMember>("members")
    .find(and(
        DbMember::guildId eq this.id,
        DbMember::deleteAt eq null
    )).toList()
