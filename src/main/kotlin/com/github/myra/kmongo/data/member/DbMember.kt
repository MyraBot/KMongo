@file:Suppress("unused")

package com.github.myra.kmongo.data.member

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.GuildMember
import com.github.myra.kmongo.cache.dbMembers
import com.github.myraBot.diskord.common.entities.guild.Member
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.toList
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
    var voiceCallTime: Long,
    var balance: Int,
    var dailyStreak: Int,
    var lastClaim: Long,
    val rankBackground: String
) {
    suspend fun setDeletionDate(millis: Long?) = dbMembers.update(GuildMember(this.guildId, this.userId), { it.deleteAt = millis }, setValue(DbMember::deleteAt, millis))
}

suspend fun Member.setDeletionDate(millis: Long?) = dbMembers.update(GuildMember(this.guildId, this.id), { it.deleteAt = millis }, setValue(DbMember::deleteAt, millis))
suspend fun Member.getLevel(): Int = dbMembers.load(GuildMember(this.guildId, this.id)).level
suspend fun Member.increaseLevel() = dbMembers.update(GuildMember(this.guildId, this.id), { it.level++ }, inc(DbMember::level, 1))
suspend fun Member.setLevel(level: Int) = dbMembers.update(GuildMember(this.guildId, this.id), { it.level = level }, setValue(DbMember::level, level))
suspend fun Member.getXp(): Long = dbMembers.load(GuildMember(this.guildId, this.id)).xp
suspend fun Member.addXp(xp: Int) = dbMembers.update(GuildMember(this.guildId, this.id), { it.xp += xp }, inc(DbMember::xp, xp))
suspend fun Member.getMessages(): Long = dbMembers.load(GuildMember(this.guildId, this.id)).messages
suspend fun Member.increaseMessages() = dbMembers.update(GuildMember(this.guildId, this.id), { it.messages++ }, inc(DbMember::messages, 1))
suspend fun Member.getVoiceCallTime(): Long = dbMembers.load(GuildMember(this.guildId, this.id)).voiceCallTime
suspend fun Member.addVoiceTime(time: Long) = dbMembers.update(GuildMember(this.guildId, this.id), { it.voiceCallTime += time }, inc(DbMember::voiceCallTime, time))
suspend fun Member.getBalance(): Int = dbMembers.load(GuildMember(this.guildId, this.id)).balance
suspend fun Member.getRank(): Int {
    val members = Mongo.getAs<DbMember>("members")
        .find(DbMember::guildId eq guildId)
        .toList()
        .toMutableList()
    members.sortWith(Comparator.comparing(DbMember::xp).reversed())
    return members.indexOfFirst { it.userId === id }
}

suspend fun Member.setBalance(balance: Int) = dbMembers.update(GuildMember(this.guildId, this.id), { it.balance = balance }, setValue(DbMember::balance, balance))
suspend fun Member.addBalance(balance: Int) = dbMembers.update(GuildMember(this.guildId, this.id), { it.balance += balance }, inc(DbMember::balance, balance))
suspend fun Member.removeBalance(balance: Int) = dbMembers.update(GuildMember(this.guildId, this.id), { it.balance -= balance }, inc(DbMember::balance, -balance))
suspend fun Member.getDailyStreak(): Int = dbMembers.load(GuildMember(this.guildId, this.id)).dailyStreak
suspend fun Member.setDailyStreak(streak: Int) = dbMembers.update(GuildMember(this.guildId, this.id), { it.dailyStreak = streak }, setValue(DbMember::dailyStreak, streak))
suspend fun Member.increaseDailyStreak() = dbMembers.update(GuildMember(this.guildId, this.id), { it.dailyStreak++ }, inc(DbMember::dailyStreak, 1))
suspend fun Member.getLastClaim(): Long = dbMembers.load(GuildMember(this.guildId, this.id)).lastClaim
suspend fun Member.updateLastClaim() = dbMembers.update(GuildMember(this.guildId, this.id), { it.lastClaim = System.currentTimeMillis() }, setValue(DbMember::lastClaim, System.currentTimeMillis()))
suspend fun Member.getRankBackground(): String = dbMembers.load(GuildMember(this.guildId, this.id)).rankBackground