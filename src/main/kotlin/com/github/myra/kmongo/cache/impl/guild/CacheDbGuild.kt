package com.github.myra.kmongo.cache.impl.guild

import com.github.m5rian.kotlingua.Lang
import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.Cache
import com.github.myra.kmongo.data.guild.DbGuild
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import kotlin.reflect.KProperty

object CacheDbGuild : Cache<DbGuild>() {
    override val collectionName: String = "guilds"
    override val key: KProperty<*> = DbGuild::guildId

    override suspend fun load(value: String): DbGuild {
        loadCache(value)
        return cache.getOrPut(value) {
            Mongo.getAs<DbGuild>(collectionName).findOne(DbGuild::guildId eq value) ?: create(value)
        }
    }

    override suspend fun create(value: String): DbGuild {
        return DbGuild(
            guildId = value,
            prefixes = mutableListOf("~"),
            _language = Lang.ENGLISH_UNITED_KINGDOM.iso,
            premium = false,
            unicorn = null,
            reactionRoles = mutableListOf(),
            autoRoles = mutableListOf(),
            logChannel = null
        )
    }

    override suspend fun update(value: String, cacheUpdate: (cache: DbGuild) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(value))
        mutex.withLock { Mongo.getAs<DbGuild>(collectionName).updateOne(and(key eq value), dbUpdate) }
    }


}