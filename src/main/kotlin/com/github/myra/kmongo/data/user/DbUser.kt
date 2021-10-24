package com.github.myra.kmongo.data.user

import com.github.myra.kmongo.cache.impl.user.CacheUser
import com.github.myra.kmongo.cache.impl.user.CacheUserSocials
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

suspend fun User.birthday(): String? = CacheUser.load(this.id).birthday
suspend fun User.socials(): DbSocials = CacheUserSocials.load(this.id)