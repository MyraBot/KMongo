@file:Suppress("unused")

package com.github.myra.kmongo.data.member

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.impl.CacheDbMember
import com.github.myraBot.diskord.common.entities.guild.Member
import kotlinx.serialization.Serializable
import org.litote.kmongo.eq
import org.litote.kmongo.inc
import org.litote.kmongo.setValue

@Serializable
data class DbMember(
        val guildId: String,
        val userId: String,
        var deleteAt: Long?,
        var level: Int,
        var xp: Long,
        var messages: Long,
        val voiceCallTime: Long,
        var balance: Int,
        var dailyStreak: Int,
        var lastClaim: Long,
        val rankBackground: String
) {
    suspend fun setDeletionDate(millis: Long?) = CacheDbMember.update(this.guildId, this.userId, { it.deleteAt = millis }, setValue(DbMember::deleteAt, millis))
}

suspend fun Member.setDeletionDate(millis: Long?) = CacheDbMember.update(this.guildId, this.id, { it.deleteAt = millis }, setValue(DbMember::deleteAt, millis))
val Member.level: Int get() = CacheDbMember.load(this.guildId, this.id).level
suspend fun Member.increaseLevel() = CacheDbMember.update(this.guildId, this.id, { it.level++ }, inc(DbMember::level, 1))
val Member.xp: Long get() = CacheDbMember.load(this.guildId, this.id).xp
suspend fun Member.addXp(xp: Int) = CacheDbMember.update(this.guildId, this.id, { it.xp += xp }, inc(DbMember::xp, xp))
val Member.messages: Long get() = CacheDbMember.load(this.guildId, this.id).messages
suspend fun Member.increaseMessages() = CacheDbMember.update(this.guildId, this.id, { it.messages++ }, inc(DbMember::messages, 1))
val Member.voiceCallTime: Long get() = CacheDbMember.load(this.guildId, this.id).voiceCallTime
val Member.balance: Int get() = CacheDbMember.load(this.guildId, this.id).balance
val Member.rank: Int
    get() {
        val members = Mongo.getAs<DbMember>("members")
            .find(DbMember::guildId eq guildId)
            .toMutableList()
        members.sortWith(Comparator.comparing(DbMember::xp).reversed())
        return members.indexOfFirst { it.userId === id }
    }

suspend fun Member.setBalance(balance: Int) = CacheDbMember.update(this.guildId, this.id, { it.balance = balance }, setValue(DbMember::balance, balance))
suspend fun Member.addBalance(balance: Int) = CacheDbMember.update(this.guildId, this.id, { it.balance += balance }, inc(DbMember::balance, balance))
suspend fun Member.removeBalance(balance: Int) = CacheDbMember.update(this.guildId, this.id, { it.balance -= balance }, inc(DbMember::balance, -balance))
val Member.dailyStreak: Int get() = CacheDbMember.load(this.guildId, this.id).dailyStreak
suspend fun Member.setDailyStreak(streak: Int) = CacheDbMember.update(this.guildId, this.id, { it.dailyStreak = streak }, setValue(DbMember::dailyStreak, streak))
suspend fun Member.increaseDailyStreak() = CacheDbMember.update(this.guildId, this.id, { it.dailyStreak++ }, inc(DbMember::dailyStreak, 1))
val Member.lastClaim: Long get() = CacheDbMember.load(this.guildId, this.id).lastClaim
suspend fun Member.updateLastClaim() = CacheDbMember.update(this.guildId, this.id, { it.lastClaim = System.currentTimeMillis() }, setValue(DbMember::lastClaim, System.currentTimeMillis()))
val Member.rankBackground: String get() = CacheDbMember.load(this.guildId, this.id).rankBackground