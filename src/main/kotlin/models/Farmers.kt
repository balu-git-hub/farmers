package com.payir.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object Farmers : Table("farmers") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val userId = uuid("user_id").uniqueIndex().references(Users.id)
    val name = text("name")
    val aadhaarNumber = text("aadhaar_number").nullable().uniqueIndex()
    val district = text("district")
    val taluk = text("taluk")
    val village = text("village")
    val address = text("address").nullable()
    val landRegistrationNumber = text("land_registration_number").nullable()
    val verificationStatus = text("verification_status").default("PENDING") // PENDING, APPROVED, REJECTED
    val verifiedBy = uuid("verified_by").nullable()
    val verifiedAt = timestamp("verified_at").nullable()
    val rejectionReason = text("rejection_reason").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val createdBy = reference("created_by", Users.id).nullable()
val updatedBy = reference("updated_by", Users.id).nullable()

}

