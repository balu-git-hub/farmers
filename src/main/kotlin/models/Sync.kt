package com.payir.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object SeedMetadata : Table("seed_metadata") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val version = integer("version").uniqueIndex()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}

object DeviceSyncState : Table("device_sync_state") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val userId = uuid("user_id").references(Users.id)
    val deviceId = text("device_id")
    val lastSyncAt = timestamp("last_sync_at").nullable()
    val lastSeedVersion = integer("last_seed_version").nullable()
}

