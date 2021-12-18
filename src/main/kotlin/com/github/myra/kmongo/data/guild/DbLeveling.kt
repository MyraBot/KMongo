package com.github.myra.kmongo.data.guild

import com.github.myra.kmongo.cache.impl.guild.CacheDbGuildLeveling
import kotlinx.serialization.Serializable
import org.litote.kmongo.eq
import org.litote.kmongo.pull
import org.litote.kmongo.pullByFilter
import org.litote.kmongo.setValue

@Serializable
data class DbLeveling(
        val guildId: String,
        val toggled: Boolean,
        val boost: Int,
        var channel: String?,
        val uniqueRoles: Boolean,
        val roles: MutableList<DbLevelingRole>
) {
    suspend fun removeRole(id: String) =
        CacheDbGuildLeveling.update(this.guildId, { dbLeveling -> dbLeveling.roles.removeIf { role -> role.id == id } }, pullByFilter(DbLeveling::roles, DbLevelingRole::id eq id))

    suspend fun removeRole(role: DbLevelingRole) = CacheDbGuildLeveling.update(this.guildId, { it.roles.remove(role) }, pull(DbLeveling::roles, role))
    suspend fun setChannel(id: String?) = CacheDbGuildLeveling.update(this.guildId, { it.channel = id }, setValue(DbLeveling::channel, id))
}

@Serializable
data class DbLevelingRole(
        val id: String,
        val level: Int
)
