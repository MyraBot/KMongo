package com.github.myra.kmongo.cache.impl.user

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.Cache
import com.github.myra.kmongo.cache.impl.guild.CacheDbGuildEconomy
import com.github.myra.kmongo.data.user.DbSocials
import com.github.myra.kmongo.data.user.DbUser
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import kotlin.reflect.KProperty

object CacheDbUserSocials : Cache<DbSocials>() {
    override val collectionName: String = "usersSocials"
    override val key: KProperty<*> = DbSocials::userId

    override suspend fun load(value: String): DbSocials {
        loadCache(value)
        return cache.getOrPut(value) {
            Mongo.getAs<DbSocials>(collectionName).findOne(DbSocials::userId eq value) ?: create(value)
        }
    }

    override suspend fun create(value: String): DbSocials {
        return DbSocials(
            userId = value,
            youtube = null
        )
    }

    override suspend fun update(value: String, cacheUpdate: (cache: DbSocials) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(value))
        mutex.withLock { Mongo.getAs<DbSocials>(collectionName).updateOne(and(key eq value), dbUpdate) }
    }

    override fun set(value: String, data: DbSocials) {
        this.cache[value] = data
        Mongo.getAs<DbSocials>(collectionName).findOneAndReplace(DbSocials::userId eq value, data)
    }

}