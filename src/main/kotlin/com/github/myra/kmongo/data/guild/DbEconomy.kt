package com.github.myra.kmongo.data.guild

import kotlinx.serialization.Serializable

@Serializable
data class DbEconomy(
        val guildId: String,
        val currency: String,
        val shop: MutableList<DbShopItem>
)