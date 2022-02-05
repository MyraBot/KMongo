package com.github.myra.kmongo.cache

import com.github.myra.kmongo.Mongo
import com.mongodb.reactivestreams.client.MongoCollection
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class Cache<I : Any, V : Any> {
    lateinit var collection: MongoCollection<V>
    lateinit var filter: (I) -> Bson
    lateinit var default: suspend (I) -> V

    val mutex: Mutex = Mutex()
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private val schedulers: MutableMap<I, ScheduledFuture<*>> = mutableMapOf()
    internal val cache: MutableMap<I, V> = mutableMapOf()

    fun loadExpiry(identifier: I) {
        if (!Mongo.connectionStringIsInitialized()) throw Exception("Before loading a Guild you need to connect to the database using Mongo#connect")

        if (schedulers.containsKey(identifier)) {
            schedulers[identifier]?.cancel(false)
            schedulers.remove(identifier)
        }
        val scheduler: ScheduledFuture<*> = executor.schedule({ cache.remove(identifier) }, 5, TimeUnit.MINUTES)
        schedulers[identifier] = scheduler
    }

    suspend fun delete(identifier: I) {
        cache.remove(identifier)
        collection.deleteOne(filter.invoke(identifier)).awaitFirstOrNull()
    }

    suspend fun load(identifier: I): V {
        loadExpiry(identifier)
        return cache.getOrPut(identifier) { collection.find(filter.invoke(identifier)).awaitFirstOrNull() ?: default.invoke(identifier) }
    }

    suspend fun update(identifier: I, cacheUpdate: (cache: V) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(identifier))
        mutex.withLock { collection.updateOne(filter.invoke(identifier), dbUpdate) }
    }

    suspend fun set(value: I, data: V) {
        this.cache[value] = data
        collection.findOneAndReplace(filter.invoke(value), data).awaitFirstOrNull()
    }

}

internal fun <K : Any, V : Any> cache(cache: Cache<K, V>.() -> Unit) = Cache<K, V>().apply(cache)