package com.github.myra.kmongo.data.user

import com.github.myra.kmongo.cache.dbUsers
import com.github.myra.kmongo.cache.dbUserSocials
import com.github.myraBot.diskord.common.entities.User
import kotlinx.serialization.Serializable

@Serializable
data class DbUser(
    val id: String,
    val name: String,
    val discriminator: String,
    val avatar: String,
    val badges: MutableList<String>,
    val birthday: String?,
    val achievements: DbAchievements,
)

suspend fun User.birthday(): String? = dbUsers.load(this.id).birthday
suspend fun User.socials(): DbSocials = dbUserSocials.load(this.id)