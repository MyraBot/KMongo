package com.github.myra.kmongo.cache

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.data.member.DbMember
import kotlinx.coroutines.sync.Mutex
import org.bson.conversions.Bson
import org.litote.kmongo.eq
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty

abstract class Cache<T : Any> {
    abstract val collectionName: String
    abstract val key: KProperty<*>

    val mutex: Mutex = Mutex()
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private val schedulers: MutableMap<String, ScheduledFuture<*>> = mutableMapOf()
    internal val cache: MutableMap<String, T> = mutableMapOf()

    fun loadCache(value: String) {
        if (!Mongo.connectionStringIsInitialized()) throw Exception("Before loading a Guild you need to connect to the database using Mongo#connect")

        if (schedulers.containsKey(value)) {
            schedulers[value]?.cancel(false)
            schedulers.remove(value)
        }
        val scheduler: ScheduledFuture<*> = executor.schedule({
            cache.remove(value)
        }, 5, TimeUnit.MINUTES)
        schedulers[value] = scheduler
    }


    fun delete(key: String) {
        cache.remove(key)
        Mongo.get("members").deleteOne(this.key eq key)
    }

    abstract suspend fun load(value: String): T

    abstract suspend fun create(value: String): T

    abstract suspend fun update(value: String, cacheUpdate: (cache: T) -> Unit, dbUpdate: Bson)
}