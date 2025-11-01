package com.payir.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object Users : Table("users") {
    val id = uuid("id").autoGenerate()

    // 🔑 FIX 1: EXPLICITLY DEFINE PRIMARY KEY
    override val primaryKey = PrimaryKey(id)
    // This tells the database layer to mark 'id' as the PRIMARY KEY,
    // which is a unique constraint required for foreign keys to reference.

    val username = text("username").uniqueIndex()
    val passwordHash = text("password_hash")
    val role = text("role") // FARMER, ADMIN, AGRI_DEPT
    val email = text("email").nullable()
    val phone = text("phone").nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    // The reference() syntax is cleaner for self-referencing and correct
    val createdBy = reference("created_by", id).nullable()
    val updatedBy = reference("updated_by", id).nullable()
}