package com.github.myra.kmongo

import com.github.m5rian.kotlingua.Lang
import com.github.myra.kmongo.data.user.DbAchievements
import com.mongodb.CursorType
import com.mongodb.client.model.Filters
import kotlinx.coroutines.launch
import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import java.util.*


object MongoUpdater {

    fun convertJavaGuilds() {
        val documentCount = KMongo.createClient(Mongo.connectionString).getDatabase("Myra").getCollection<Document>("guilds").countDocuments(Document())
        val documentsPerRun = 500
        var passedDocuments = 0

        val runs = documentCount / documentsPerRun

        val guildDocuments: MutableSet<Document> = mutableSetOf()
        val guildEconomyDocuments: MutableSet<Document> = mutableSetOf()
        val guildLevelingDocuments: MutableSet<Document> = mutableSetOf()
        val guildSuggestions: MutableSet<Document> = mutableSetOf()
        val guildYoutubeNotifications: MutableSet<Document> = mutableSetOf()
        val guildTwitchNotifications: MutableSet<Document> = mutableSetOf()
        val guildWelcomingDocuments: MutableSet<Document> = mutableSetOf()

        Mongo.get("guilds").deleteMany(Filters.exists("_id"))
        Mongo.get("guildsEconomy").deleteMany(Filters.exists("_id"))
        Mongo.get("guildsLeveling").deleteMany(Filters.exists("_id"))
        Mongo.get("guildsSuggestions").deleteMany(Filters.exists("_id"))
        Mongo.get("guildsYoutube").deleteMany(Filters.exists("_id"))
        Mongo.get("guildsTwitch").deleteMany(Filters.exists("_id"))
        Mongo.get("guildsWelcoming").deleteMany(Filters.exists("_id"))

        val collection = KMongo.createClient(Mongo.connectionString).getDatabase("Myra").getCollection<Document>("guilds")
        for (run in 0..runs) {
            Mongo.coroutineScope.launch {
                println("Started coroutine #$run")
                val cursor = collection
                    .find(Document())
                    .skip((run * documentsPerRun).toInt())
                    .limit(documentsPerRun)
                    .cursorType(CursorType.NonTailable)
                    .cursor()
                while (cursor.hasNext()) {
                    passedDocuments++
                    val document = cursor.next()

                    val economyShop: MutableList<Document> = mutableListOf()
                    document.get("economy", Document::class.java).get("shop", Document::class.java).entries.forEach {
                        val value: Document = it.value as Document
                        economyShop.add(
                            Document()
                                .append("id", it.key)
                                .append("price", getLong(value, "price"))
                        )
                    }
                    val economy = Document()
                        .append("guildId", document.getString("guildId"))
                        .append("currency", document.get("economy", Document::class.java).getString("currency"))
                        .append("shop", economyShop)
                    guildEconomyDocuments.add(economy)

                    val lvlDoc = document.get("leveling", Document::class.java)
                    val levelingRoles: MutableList<Document> = mutableListOf()
                    lvlDoc.get("roles", Document::class.java).entries.forEach {
                        val value: Document = it.value as Document
                        levelingRoles.add(
                            Document()
                                .append("id", it.key)
                                .append("level", value.getInteger("level"))
                        )
                    }
                    val leveling = Document()
                        .append("guildId", document.getString("guildId"))
                        .append("toggled", document.get("listeners", Document::class.java).getBoolean("leveling"))
                        .append("boost", 1)
                        .append("uniqueRoles", lvlDoc.getBoolean("uniqueRoles"))
                        .append("roles", levelingRoles)
                        .append("channel", if (lvlDoc.getString("channel") == "not set") null else lvlDoc.getString("channel"))
                    guildLevelingDocuments.add(leveling)

                    val suggestions = Document()
                        .append("guildId", document.getString("guildId"))
                        .append("toggled", document.get("listeners", Document::class.java).getBoolean("leveling"))
                        .append("channel", document.getString("suggestionsChannel"))
                    guildSuggestions.add(suggestions)

                    val notiDoc = document.get("notifications", Document::class.java)

                    val yt = Document()
                        .append("guildId", document.getString("guildId"))
                        .append("channel", if (notiDoc.getString("channel") == "not set") null else notiDoc.getString("channel"))
                        .append("message", if (notiDoc.getString("youtubeMessage") == "not set") null else notiDoc.getString("youtubeMessage"))
                        .append("subscriptions", notiDoc.getList("youtube", String::class.java))
                    guildYoutubeNotifications.add(yt)

                    val twitch = Document()
                        .append("guildId", document.getString("guildId"))
                        .append("channel", if (notiDoc.getString("channel") == "not set") null else notiDoc.getString("channel"))
                        .append("message", if (notiDoc.getString("twitchMessage") == "not set") null else notiDoc.getString("twitchMessage"))
                        .append("subscriptions", notiDoc.getList("twitch", String::class.java))
                    guildTwitchNotifications.add(twitch)

                    val welcomeDoc = document["welcome", Document::class.java]
                    val welcome = Document()
                        .append("guildId", document.getString("guildId"))
                        .append("channel", welcomeDoc.getString("welcomeChannel"))
                        .append("directMessage", Document()
                            .append("toggled", false)
                            .append("message",
                                    if (welcomeDoc.getString("welcomeDirectMessage") == "Welcome {user} to {server}! Enjoy your stay") null else welcomeDoc.getString("welcomeDirectMessage")))
                        .append("embed", Document()
                            .append("toggled", false)
                            .append("message",
                                    if (welcomeDoc.getString("welcomeEmbedMessage") == "Welcome {user} to {server}! Enjoy your stay") null else welcomeDoc.getString("welcomeEmbedMessage"))
                            .append("colour", welcomeDoc.getString("welcomeColour")))
                        .append("image", Document()
                            .append("toggled", false)
                            .append("image", welcomeDoc.getString("welcomeImageBackground"))
                            .append("font", "default"))
                    guildWelcomingDocuments.add(welcome)

                    val guild = Document()
                        .append("guildId", document.getString("guildId"))
                        .append("language", Lang.ENGLISH_UNITED_KINGDOM.iso)
                        .append("premium", document.getBoolean("premium"))
                        .append("reactionRoles", document.getList("reactionRoles", Document::class.java))
                        .append("autoRoles", document.getList("autoRole", String::class.java))
                        .append("logChannel", document.getString("logChannel"))
                        .append("commands", document.get("commands", Document::class.java))
                    guildDocuments.add(guild)

                    val percent = (passedDocuments.toDouble() / documentCount) * 100
                    println("Updating Guilds... Status: ${percent.toInt()}% ($passedDocuments/$documentCount)")
                }
                if (run == runs) {
                    println("waiting 5 secs")
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            Mongo.get("guilds").insertMany(guildDocuments.toList())
                            Mongo.get("guildsEconomy").insertMany(guildEconomyDocuments.toList())
                            Mongo.get("guildsLeveling").insertMany(guildLevelingDocuments.toList())
                            Mongo.get("guildsSuggestions").insertMany(guildSuggestions.toList())
                            Mongo.get("guildsYoutube").insertMany(guildYoutubeNotifications.toList())
                            Mongo.get("guildsTwitch").insertMany(guildTwitchNotifications.toList())
                            Mongo.get("guildsWelcoming").insertMany(guildWelcomingDocuments.toList())
                        }
                    }, 5000)

                }
                println("Coroutine #$run finished")
            }
        }


    }

    fun convertJavaUsers() {
        //Mongo.get("users").updateMany(EMPTY_BSON, setValue(KPrope<String>() "name", "test name"))

        val documentCount = KMongo.createClient(Mongo.connectionString).getDatabase("Myra").getCollection<Document>("users").countDocuments(Document())
        val documentsPerRun = 500
        var passedDocuments = 0

        val runs = documentCount / documentsPerRun
        println(
            "Documents to iterate through: $documentCount\n" +
                    "Documents per run: $documentsPerRun\n" +
                    "Total runs to do: $runs"
        )

        val docs: MutableSet<Document> = mutableSetOf()
        val membrs = mutableListOf<Document>()
        val collection = KMongo.createClient(Mongo.connectionString).getDatabase("Myra").getCollection<Document>("users")
        for (run in 0..runs) {
            Mongo.coroutineScope.launch {
                println("Started coroutine #$run")
                val cursor = collection
                    .find(Document())
                    .skip((run * documentsPerRun).toInt())
                    .limit(documentsPerRun)
                    .cursorType(CursorType.NonTailable)
                    .cursor()
                while (cursor.hasNext()) {
                    passedDocuments++
                    val document = cursor.next()

                    val updatedDocument = Document()
                        .append("id", document.getString("userId"))
                        .append("name", document.getString("name"))
                        .append("discriminator", document.getString("discriminator"))
                        .append("avatar", document.getString("avatar"))
                        .append("badges", document.getList("badges", String::class.java))
                        .append("birthday", null)
                        .append("achievements", DbAchievements())

                    // Go through all guild member documents
                    document.entries.forEach {
                        if (it.key.matches("\\d*".toRegex())) {
                            val guild = it.value as Document
                            membrs.add(
                                Document()
                                    .append("guildId", it.key)
                                    .append("userId", document.getString("userId"))
                                    .append("deleteAt", null)
                                    .append("level", guild.getInteger("level"))
                                    .append("xp", getLong(guild, "xp"))
                                    .append("messages", getLong(guild, "messages"))
                                    .append("voiceCallTime", guild.getLong("voiceCallTime"))
                                    .append("balance", guild.getInteger("balance"))
                                    .append("dailyStreak", guild.getInteger("dailyStreak"))
                                    .append("lastClaim", guild.getLong("lastClaim"))
                                    .append("rankBackground", guild.getString("rankBackground"))
                            )
                        }
                    }

                    // Print out progress
                    val percent = (passedDocuments.toDouble() / documentCount) * 100
                    println("Updating Users... Status: ${percent.toInt()}% ($passedDocuments/$documentCount)")

                    // If my own document comes put it on the top of the list
                    if (updatedDocument.getString("name") == "not set") continue
                    //Mongo.get("users").insertOne(updatedDocument)
                    if (updatedDocument.getString("id") == "639544573114187797") {
                        val toMutableList = docs.toMutableList()
                        docs.clear()
                        docs.add(updatedDocument)
                        docs.addAll(toMutableList)
                    } else {
                        docs.add(updatedDocument)
                    }

                }
                if (run == runs) {
                    println("waiting 5 secs")
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            Mongo.get("users").deleteMany(Filters.exists("_id"))
                            Mongo.get("members").deleteMany(Filters.exists("_id"))

                            Mongo.get("users").insertMany(docs.filter { !it.isEmpty() }.toList())
                            Mongo.get("members").insertMany(membrs.filter { !it.isEmpty() }.toMutableList())
                        }
                    }, 5000)

                }
                println("Coroutine #$run finished")
            }
        }


    }


    private fun getLong(document: Document, key: String): Long {
        return try {
            document.getLong(key)
        } catch (e: ClassCastException) {
            document.getInteger(key).toLong()
        }
    }

}