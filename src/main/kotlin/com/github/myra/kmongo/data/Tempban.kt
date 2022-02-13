package com.github.myra.kmongo.data

import com.github.myra.kmongo.Mongo
import kotlinx.coroutines.reactive.awaitFirst
import org.litote.kmongo.and
import org.litote.kmongo.eq

@kotlinx.serialization.Serializable
data class Tempban(
    val userId: String,
    val guildId: String,
    val moderatorId: String,
    val unbanTime: Long
) {
    suspend fun delete() {
        Mongo.getAs<Tempban>("tempbans").deleteOne(and(
            Tempban::userId eq this.userId,
            Tempban::guildId eq this.guildId
        )).awaitFirst()
    }
}
