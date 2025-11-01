package com.payir.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object CropPlantings : Table("crop_plantings") {
    val id = uuid("id").autoGenerate()
    override val primaryKey = PrimaryKey(id)
    val farmerId = uuid("farmer_id").references(Farmers.id)
    val cropType = text("crop_type") // PADDY, WHEAT, CORN, etc.
    val season = text("season") // KHARIF, RABI, ZAID
    val year = integer("year")
    val areaMeasurement = decimal("area_measurement", 10, 2)
    val measurementUnit = text("measurement_unit").default("ACRES") // ACRES, HECTARES
    val district = text("district")
    val taluk = text("taluk")
    val village = text("village")
    val plotDetails = text("plot_details").nullable()
    val plantingDate = date("planting_date").nullable()
    val expectedHarvestDate = date("expected_harvest_date").nullable()
    val status = text("status").default("PLANTED") // PLANTED, GROWING, HARVESTED, CANCELLED
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val createdBy = reference("created_by", Users.id).nullable()
    val updatedBy = reference("updated_by", Users.id).nullable()

}

