package com.github.myra.kmongo.cache.impl.guild

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.Cache
import com.github.myra.kmongo.data.guild.DbEconomy
import com.github.myra.kmongo.data.guild.DbLeveling
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import kotlin.reflect.KProperty

object CacheGuildLeveling : Cache<DbLeveling>() {
    override val collectionName: String = "guildsLeveling"
    override val key: KProperty<*> = DbLeveling::guildId

    override suspend fun load(value: String): DbLeveling {
        loadCache(value)
        return cache.getOrPut(value) {
            Mongo.getAs<DbLeveling>(collectionName).findOne(DbLeveling::guildId eq value) ?: create(value)
        }
    }

    override suspend fun create(value: String): DbLeveling {
        return DbLeveling(
            guildId = value,
            toggled = true,
            boost = 1,
            channel = null,
            uniqueRoles = false,
            roles = mutableListOf()
        )
    }

    override suspend fun update(value: String, cacheUpdate: (cache: DbLeveling) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(value))
        CacheGuildEconomy.mutex.withLock { Mongo.getAs<DbLeveling>(collectionName).updateOne(and(key eq value), dbUpdate) }
    }

}