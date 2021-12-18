package com.github.myra.kmongo.cache.impl.guild

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.Cache
import com.github.myra.kmongo.data.guild.DbEconomy
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import kotlin.reflect.KProperty

object CacheDbGuildEconomy : Cache<DbEconomy>() {
    override val collectionName: String = "guildsEconomy"
    override val key: KProperty<*> = DbEconomy::guildId

    override suspend fun load(value: String): DbEconomy {
        loadCache(value)
        return cache.getOrPut(value) {
            Mongo.getAs<DbEconomy>(collectionName).findOne(DbEconomy::guildId eq value) ?: create(value)
        }
    }

    override suspend fun create(value: String): DbEconomy {
        return DbEconomy(
            guildId = value,
            currency = "bla",
            shop = mutableListOf()
        )
    }

    override suspend fun update(value: String, cacheUpdate: (cache: DbEconomy) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(value))
        mutex.withLock { Mongo.getAs<DbEconomy>(collectionName).updateOne(and(key eq value), dbUpdate) }
    }

}