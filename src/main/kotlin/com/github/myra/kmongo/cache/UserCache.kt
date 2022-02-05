package com.github.myra.kmongo.cache

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.data.member.DbMember
import com.github.myra.kmongo.data.user.DbAchievements
import com.github.myra.kmongo.data.user.DbSocials
import com.github.myra.kmongo.data.user.DbUser
import com.github.myraBot.diskord.common.Diskord
import org.litote.kmongo.and
import org.litote.kmongo.eq

val dbUsers = cache<String, DbUser> {
    collection = Mongo.getAs("users")
    filter = { DbUser::id eq it }
    default = {
        val user = Diskord.getUser(it).awaitNonNull()
        DbUser(
            id = it,
            name = user.username,
            discriminator = user.discriminator,
            avatar = user.avatar,
            badges = mutableListOf(),
            birthday = null,
            achievements = DbAchievements(
                inviteMyra = false
            )
        )
    }
}

val dbUserSocials = cache<String, DbSocials> {
    collection = Mongo.getAs("userSocials")
    filter = { DbSocials::userId eq it }
    default = {
        DbSocials(
            userId = it,
            youtube = null
        )
    }
}

data class GuildMember(val guildId: String, val userId: String)

val dbMembers = cache<GuildMember, DbMember> {
    collection = Mongo.getAs("members")
    filter = { and(DbMember::guildId eq it.guildId, DbMember::userId eq it.userId) }
    default = {
        DbMember(
            guildId = it.guildId,
            userId = it.userId,
            deleteAt = null,
            level = 0,
            xp = 0,
            messages = 0,
            voiceCallTime = 0,
            balance = 0,
            dailyStreak = 0,
            lastClaim = System.currentTimeMillis(),
            rankBackground = "default"
        )
    }
}