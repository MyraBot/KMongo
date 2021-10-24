package com.github.myra.kmongo.data.guild

import kotlinx.serialization.Serializable

@Serializable
data class DbShopItem(
    val id: String,
    val price: Long
)
