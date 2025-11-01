package com.payir.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object FarmerDocuments : Table("farmer_documents") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val farmerId = uuid("farmer_id").references(Farmers.id)
    val documentType = text("document_type") // LAND_REGISTRATION, BANK_STATEMENT, GOVT_ID
    val documentName = text("document_name")
    val filePath = text("file_path")
    val fileSizeBytes = long("file_size_bytes").nullable()
    val mimeType = text("mime_type").nullable()
    val verificationStatus = text("verification_status").default("PENDING") // PENDING, VERIFIED, REJECTED
    val verifiedBy = uuid("verified_by").nullable()
    val verifiedAt = timestamp("verified_at").nullable()
    val rejectionReason = text("rejection_reason").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val createdBy = reference("created_by", Users.id).nullable()
val updatedBy = reference("updated_by", Users.id).nullable()

}

