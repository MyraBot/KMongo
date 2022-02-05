package com.github.myra.kmongo.cache

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.data.guild.*
import org.litote.kmongo.eq

val settings = cache<String, DbGuild> {
    collection = Mongo.getAs("guilds")
    filter = { DbEconomy::guildId eq it }
    default = {
        DbGuild(
            guildId = it,
            premium = false,
            unicorn = null,
            reactionRoles = mutableListOf(),
            autoRoles = mutableListOf(),
            logChannel = null
        )
    }
}

val dbEconomy = cache<String, DbEconomy> {
    collection = Mongo.getAs("guildsEconomy")
    filter = { DbEconomy::guildId eq it }
    default = {
        DbEconomy(
            guildId = it,
            currency = "<:Coin:775280695710580738>",
            shop = mutableListOf()
        )
    }
}

val dbLeveling = cache<String, DbLeveling> {
    collection = Mongo.getAs("guildsLeveling")
    filter = { DbLeveling::guildId eq it }
    default = {
        DbLeveling(
            guildId = it,
            toggled = true,
            boost = 1,
            channel = null,
            uniqueRoles = false,
            roles = mutableListOf()
        )
    }
}

val dbTwitch = cache<String, DbTwitch> {
    collection = Mongo.getAs("guildsTwitch")
    filter = { DbTwitch::guildId eq it }
    default = {
        DbTwitch(
            guildId = it,
            channel = null,
            subscriptions = mutableListOf(),
            message = null
        )
    }
}

val dbYoutube = cache<String, DbYoutube> {
    collection = Mongo.getAs("guildsYoutube")
    filter = { DbYoutube::guildId eq it }
    default = {
        DbYoutube(
            guildId = it,
            channel = null,
            subscriptions = mutableListOf(),
            message = null
        )
    }
}

val dbWelcoming = cache<String, DbWelcoming> {
    collection = Mongo.getAs("guildsWelcoming")
    filter = { DbWelcoming::guildId eq it }
    default = {
        DbWelcoming(
            guildId = it,
            channel = null,
            directMessage = DbDirectMessage(false, null),
            embed = DbEmbed(false, null, null),
            image = DbImage(false, null, "default")
        )
    }
}

val dbSuggestions = cache<String, DbSuggestions> {
    collection = Mongo.getAs("guildsSuggestions")
    filter = { DbSuggestions::guildId eq it }
    default = {
        DbSuggestions(
            guildId = it,
            toggled = true,
            channel = null
        )
    }
}