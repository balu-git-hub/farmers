package com.payir.models

import org.jetbrains.exposed.sql.Table

object Districts : Table("districts") {
    val id = uuid("id").autoGenerate() // This column is referenced by Taluks

    // 🔑 ADD THIS LINE TO EXPLICITLY SET IT AS THE PRIMARY KEY
    override val primaryKey = PrimaryKey(id)

    val name = text("name").uniqueIndex()
    val state = text("state").nullable()
    val code = text("code").uniqueIndex().nullable()
}

// Taluks and Villages remain correct:
object Taluks : Table("taluks") {
    val id = uuid("id").autoGenerate()
    val districtId = uuid("district_id").references(Districts.id) // This reference will now pass
    val name = text("name")
    val code = text("code").uniqueIndex().nullable()
    override val primaryKey = PrimaryKey(id) // Optional: Add this to Taluks and Villages for completeness
}

object Villages : Table("villages") {
    val id = uuid("id").autoGenerate()
    val talukId = uuid("taluk_id").references(Taluks.id)
    val name = text("name")
    val code = text("code").uniqueIndex().nullable()
    override val primaryKey = PrimaryKey(id) // Optional: Add this to Taluks and Villages for completeness
}