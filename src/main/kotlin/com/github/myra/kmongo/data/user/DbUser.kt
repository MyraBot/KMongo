package com.github.myra.kmongo.data.user

import com.github.myra.kmongo.cache.impl.user.CacheDbUser
import com.github.myra.kmongo.cache.impl.user.CacheDbUserSocials
import com.github.myraBot.diskord.common.entities.User
import kotlinx.serialization.Serializable

@Serializable
data class DbUser(
        val userId: String,
        val name: String,
        val discriminator: String,
        val avatar: String,
        val badges: MutableList<String>,
        val birthday: String?,
        val achievements: DbAchievements,
)

suspend fun User.birthday(): String? = CacheDbUser.load(this.id).birthday
suspend fun User.socials(): DbSocials = CacheDbUserSocials.load(this.id)