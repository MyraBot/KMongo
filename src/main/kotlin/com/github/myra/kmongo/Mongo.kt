package com.github.myra.kmongo

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.internal.MongoDatabaseImpl
import de.undercouch.bson4jackson.serializers.BsonSerializers
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.serializer
import org.bson.Document
import org.bson.conversions.Bson
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
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
        return mongo.getCollection<T>(collection)
    }

    fun connectionStringIsInitialized(): Boolean {
        return this::connectionString.isInitialized
    }

}

fun database(database: Mongo.() -> Unit) = Mongo.apply(database)