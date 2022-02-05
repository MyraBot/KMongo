package com.github.myra.kmongo

import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import org.bson.Document
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.reactivestreams.getCollectionOfName
import org.litote.kmongo.serialization.registerSerializer

object Mongo {
    lateinit var connectionString: String
    lateinit var database: String
    lateinit var coroutineScope: CoroutineScope
    lateinit var colour: String

    lateinit var mongo: MongoDatabase

    fun connect() {
        mongo = KMongo.createClient(connectionString).getDatabase(database)
        registerSerializer(ColorHexSerializer)
    }

    fun get(collection: String): MongoCollection<Document> {
        return mongo.getCollection(collection)
    }

    inline fun <reified T : Any> getAs(collection: String): MongoCollection<T> {
        return mongo.getCollectionOfName(collection)
    }

    fun connectionStringIsInitialized(): Boolean {
        return this::connectionString.isInitialized
    }

}

fun database(database: Mongo.() -> Unit) = Mongo.apply(database)