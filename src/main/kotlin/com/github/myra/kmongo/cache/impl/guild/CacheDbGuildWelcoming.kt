package com.github.myra.kmongo.cache.impl.guild

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.Cache
import com.github.myra.kmongo.data.guild.*
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import kotlin.reflect.KProperty

object CacheDbGuildWelcoming : Cache<DbWelcoming>() {
    override val collectionName: String = "guildsWelcoming"
    override val key: KProperty<*> = DbWelcoming::guildId

    override suspend fun load(value: String): DbWelcoming {
        loadCache(value)
        return cache.getOrPut(value) {
            Mongo.getAs<DbWelcoming>(collectionName).findOne(DbWelcoming::guildId eq value) ?: create(value)
        }
    }

    override suspend fun create(value: String): DbWelcoming {
        return DbWelcoming(
            guildId = value,
            channel = null,
            directMessage = DbDirectMessage(false, null),
            embed = DbEmbed(false, null, Mongo.colour),
            image = DbImage(false, null, "default")
        )
    }

    override suspend fun update(value: String, cacheUpdate: (cache: DbWelcoming) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(value))
        mutex.withLock { Mongo.getAs<DbWelcoming>(collectionName).updateOne(and(key eq value), dbUpdate) }
    }

    override fun set(value: String, data: DbWelcoming) {
        this.cache[value] = data
        Mongo.getAs<DbWelcoming>(collectionName).findOneAndReplace(DbWelcoming::guildId eq value, data)
    }

}