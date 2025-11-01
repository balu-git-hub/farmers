package com.payir.models
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp


object Admin : Table("admin") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val userId = uuid("user_id").uniqueIndex().references(Users.id)
    val name = text("name")
    val email = text("email").nullable()
    val phone = text("phone").nullable()
    val designation = text("designation").nullable()
    val department = text("department").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val createdBy = reference("created_by", Users.id).nullable()
    val updatedBy = reference("updated_by", Users.id).nullable()

}

