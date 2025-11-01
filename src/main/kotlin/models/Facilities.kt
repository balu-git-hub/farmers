package com.payir.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.*


object Facilities : Table("facilities") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val name = text("name")
    val type = text("type") // GOVERNMENT, PRIVATE_MILL, COMMUNITY
    val district = text("district")
    val taluk = text("taluk")
    val village = text("village")
    val address = text("address").nullable()
    val totalCapacitySacks = integer("total_capacity_sacks")
    val availableCapacitySacks = integer("available_capacity_sacks")
    val pricePerSack = decimal("price_per_sack", 10, 2).nullable()
    val contactName = text("contact_name").nullable()
    val contactPhone = text("contact_phone").nullable()
    val ownerType = text("owner_type").nullable()
    val description = text("description").nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val createdBy = reference("created_by", Users.id).nullable()
val updatedBy = reference("updated_by", Users.id).nullable()

}

object FacilitiesHistory : Table("facilities_history") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val facilityId = uuid("facility_id").references(Facilities.id)
    val action = text("action") // CREATED, UPDATED, DELETED
    val changedFields = text("changed_fields").nullable() // JSONB stored as text
    val oldValues = text("old_values").nullable() // JSONB stored as text
    val newValues = text("new_values").nullable() // JSONB stored as text
    val changedAt = timestamp("changed_at").defaultExpression(CurrentTimestamp)
    val changedBy = reference("changed_by", Users.id).nullable()

}

