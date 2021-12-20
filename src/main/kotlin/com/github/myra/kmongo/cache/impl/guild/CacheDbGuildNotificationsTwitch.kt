package com.github.myra.kmongo.cache.impl.guild

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.Cache
import com.github.myra.kmongo.data.guild.DbLeveling
import com.github.myra.kmongo.data.guild.DbTwitch
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import kotlin.reflect.KProperty

object CacheDbGuildNotificationsTwitch : Cache<DbTwitch>() {
    override val collectionName: String = "guildsTwitch"
    override val key: KProperty<*> = DbTwitch::guildId

    override suspend fun load(value: String): DbTwitch {
        loadCache(value)
        return cache.getOrPut(value) {
            Mongo.getAs<DbTwitch>(collectionName).findOne(DbTwitch::guildId eq value) ?: create(value)
        }
    }

    override suspend fun create(value: String): DbTwitch {
        return DbTwitch(
            guildId = value,
            channel = null,
            subscriptions = mutableListOf(),
            message = null
        )
    }

    override suspend fun update(value: String, cacheUpdate: (cache: DbTwitch) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(value))
        mutex.withLock { Mongo.getAs<DbTwitch>(collectionName).updateOne(and(key eq value), dbUpdate) }
    }

    override fun set(value: String, data: DbTwitch) {
        this.cache[value] = data
        Mongo.getAs<DbTwitch>(collectionName).findOneAndReplace(DbTwitch::guildId eq value, data)
    }

}