package com.github.myra.kmongo.data.user

import com.github.myra.kmongo.cache.dbUserSocials
import com.github.myra.kmongo.cache.dbUsers
import com.github.myra.kmongo.data.member.DbMember
import com.github.myraBot.diskord.common.entities.User
import kotlinx.serialization.Serializable
import org.litote.kmongo.inc

@Serializable
data class DbUser(
    val id: String,
    val badges: MutableList<String>,
    var xp: Long,
    var messages: Long,
    val birthday: String?,
    val achievements: DbAchievements,
)

suspend fun User.birthday(): String? = dbUsers.load(this.id).birthday
suspend fun User.socials(): DbSocials = dbUserSocials.load(this.id)
suspend fun User.getXp(): Long = dbUsers.load(this.id).xp
suspend fun User.addXp(xp: Int) = dbUsers.update(this.id, { it.xp += xp }, inc(DbMember::xp, xp))
suspend fun User.getMessages(): Long = dbUsers.load(this.id).messages
suspend fun User.increaseMessages() = dbUsers.update(this.id, { it.messages++ }, inc(DbMember::messages, 1))