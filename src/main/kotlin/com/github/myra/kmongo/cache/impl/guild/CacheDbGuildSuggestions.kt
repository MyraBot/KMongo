package com.github.myra.kmongo.cache.impl.guild

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.Cache
import com.github.myra.kmongo.data.guild.DbSuggestions
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import kotlin.reflect.KProperty

object CacheDbGuildSuggestions : Cache<DbSuggestions>() {
    override val collectionName: String = "guildsSuggestions"
    override val key: KProperty<*> = DbSuggestions::guildId

    override suspend fun load(value: String): DbSuggestions {
        loadCache(value)
        return cache.getOrPut(value) {
            Mongo.getAs<DbSuggestions>(collectionName).findOne(DbSuggestions::guildId eq value) ?: create(value)
        }
    }

    override suspend fun create(value: String): DbSuggestions {
        return DbSuggestions(
            guildId = value,
            toggled = true,
            channel = null
        )
    }

    override suspend fun update(value: String, cacheUpdate: (cache: DbSuggestions) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(value))
        CacheDbGuildEconomy.mutex.withLock { Mongo.getAs<DbSuggestions>(collectionName).updateOne(and(key eq value), dbUpdate) }
    }
}