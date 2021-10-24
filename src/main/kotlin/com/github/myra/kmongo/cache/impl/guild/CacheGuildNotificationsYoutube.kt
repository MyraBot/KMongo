package com.github.myra.kmongo.cache.impl.guild

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.Cache
import com.github.myra.kmongo.data.guild.DbTwitch
import com.github.myra.kmongo.data.guild.DbYoutube
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import kotlin.reflect.KProperty

object CacheGuildNotificationsYoutube : Cache<DbYoutube>() {
    override val collectionName: String = "guildsYoutube"
    override val key: KProperty<*> = DbYoutube::guildId

    override suspend fun load(value: String): DbYoutube {
        loadCache(value)
        return cache.getOrPut(value) {
            Mongo.getAs<DbYoutube>(collectionName).findOne(DbYoutube::guildId eq value) ?: create(value)
        }
    }

    override suspend fun create(value: String): DbYoutube {
        return DbYoutube(
            guildId = value,
            channel = null,
            subscriptions = mutableListOf(),
            message = null
        )
    }

    override suspend fun update(value: String, cacheUpdate: (cache: DbYoutube) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(value))
        CacheGuildEconomy.mutex.withLock { Mongo.getAs<DbYoutube>(collectionName).updateOne(and(key eq value), dbUpdate) }
    }

}