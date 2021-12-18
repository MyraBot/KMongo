package com.github.myra.kmongo.cache.impl

import com.github.myra.kmongo.Mongo
import com.github.myra.kmongo.data.guild.DbGuild
import com.github.myra.kmongo.data.member.DbMember
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object MemberCache {
    private val mutex: Mutex = Mutex()
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private val schedulers: MutableMap<GuildMember, ScheduledFuture<*>> = mutableMapOf()
    private val cache: MutableMap<GuildMember, DbMember> = mutableMapOf()

    fun load(guildId: String, userId: String): DbMember {
        if (!Mongo.connectionStringIsInitialized()) throw Exception("Before loading a Guild you need to connect to the database using Mongo#connect")
        val guildMember = GuildMember(guildId, userId)

        if (schedulers.containsKey(guildMember)) {
            schedulers[guildMember]?.cancel(false)
            schedulers.remove(guildMember)
        }
        val scheduler: ScheduledFuture<*> = executor.schedule({
            cache.remove(guildMember)
        }, 5, TimeUnit.MINUTES)
        schedulers[guildMember] = scheduler

        return cache.getOrPut(guildMember) {
            Mongo.getAs<DbMember>("members").findOne(and(DbMember::guildId eq guildId, DbMember::userId eq userId)) ?: addMemberToDb(guildMember)
        }
    }

    private fun addMemberToDb(guildMember: GuildMember): DbMember {
        val member = DbMember(
            guildId = guildMember.guildId,
            userId = guildMember.userId,
            deleteAt = null,
            level = 0,
            xp = 0,
            messages = 0,
            voiceCallTime = 0,
            balance = 0,
            dailyStreak = 0,
            lastClaim = System.currentTimeMillis(),
            rankBackground = "default"
        )
        Mongo.getAs<DbMember>("members").insertOne(member)
        return member
    }

    suspend fun update(guildId: String, userId: String, cacheUpdate: (cache: DbMember) -> Unit, dbUpdate: Bson) {
        cacheUpdate.invoke(load(guildId, userId))
        mutex.withLock {
            Mongo.getAs<DbGuild>("members").updateOne(
                and(DbMember::guildId eq guildId, DbMember::userId eq userId),
                dbUpdate
            )
        }
    }

}

private data class GuildMember(val guildId: String, val userId: String)