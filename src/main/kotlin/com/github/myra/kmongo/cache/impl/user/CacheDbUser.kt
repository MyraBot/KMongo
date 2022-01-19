package com.github.myra.kmongo.cache.impl.user

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.cache.Cache
import com.github.myra.kmongo.data.user.DbAchievements
import com.github.myra.kmongo.data.user.DbUser
import com.github.myraBot.diskord.common.Diskord
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import kotlin.reflect.KProperty

object CacheDbUser : Cache<DbUser>() {
    override val collectionName: String = "users"
    override val key: KProperty<*> = DbUser::id

    override suspend fun load(value: String): DbUser {
        loadCache(value)
        return cache.getOrPut(value) {
            Mongo.getAs<DbUser>(collectionName).findOne(DbUser::id eq value) ?: create(value)
        }
    }

    override suspend fun create(value: String): DbUser {
        val user = Diskord.getUser(value).awaitNonNull()
        return DbUser(
            id = value,
            name = user.username,
            discriminator = user.discriminator,
            avatar = user.avatar,
            badges = mutableListOf(),
            birthday = null,
            achievements = DbAchievements(
                inviteMyra = false
            )
        )
    }

    override suspend fun update(value: String, cacheUpdate: (cache: DbUser) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(value))
        mutex.withLock { Mongo.getAs<DbUser>(collectionName).updateOne(and(key eq value), dbUpdate) }
    }

    override fun set(value: String, data: DbUser) {
        this.cache[value] = data
        Mongo.getAs<DbUser>(collectionName).findOneAndReplace(DbUser::id eq value, data)
    }

}