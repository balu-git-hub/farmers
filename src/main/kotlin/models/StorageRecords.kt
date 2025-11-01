package com.payir.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object StorageRecords : Table("storage_records") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val farmerId = uuid("farmer_id").references(Farmers.id)
    val bookingId = reference("booking_id", Bookings.id).nullable()
    val cropType = text("crop_type")
    val quantitySacks = integer("quantity_sacks")
    val storageType = text("storage_type").nullable() // HDPE_BAGS, SILO, etc.
    val moisturePercent = decimal("moisture_percent", 5, 2).nullable()
    val storageLocationDesc = text("storage_location_desc").nullable()
    val facilityId = reference("facility_id", Facilities.id).nullable()
    val storedDate = date("stored_date").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val createdBy = reference("created_by", Users.id).nullable()
val updatedBy = reference("updated_by", Users.id).nullable()

}

object StorageRecordsHistory : Table("storage_records_history") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val storageRecordId = uuid("storage_record_id").references(StorageRecords.id)
    val action = text("action") // CREATED, UPDATED, DELETED
    val changedFields = text("changed_fields").nullable() // JSON stored as text
    val oldValues = text("old_values").nullable() // JSON stored as text
    val newValues = text("new_values").nullable() // JSON stored as text
    val changedAt = timestamp("changed_at").defaultExpression(CurrentTimestamp)
    val changedBy = reference("changed_by", Users.id).nullable()
}

