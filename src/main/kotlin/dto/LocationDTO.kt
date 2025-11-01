package com.payir.dto

import kotlinx.serialization.Serializable

@Serializable
data class DistrictResponse(
    val id: String,
    val name: String,
    val state: String?,
    val code: String?
)

@Serializable
data class TalukResponse(
    val id: String,
    val districtId: String,
    val name: String,
    val code: String?
)

@Serializable
data class VillageResponse(
    val id: String,
    val talukId: String,
    val name: String,
    val code: String?
)

