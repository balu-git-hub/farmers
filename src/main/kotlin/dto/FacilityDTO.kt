package com.payir.dto

import kotlinx.serialization.Serializable

// Facility Request
@Serializable
data class CreateFacilityRequest(
    val name: String,
    val type: String, // GOVERNMENT, PRIVATE_MILL, COMMUNITY
    val district: String,
    val taluk: String,
    val village: String,
    val address: String? = null,
    val totalCapacitySacks: Int,
    val availableCapacitySacks: Int,
    val pricePerSack: Double? = null,
    val contactName: String? = null,
    val contactPhone: String? = null,
    val ownerType: String? = null,
    val description: String? = null
)

@Serializable
data class UpdateFacilityRequest(
    val name: String? = null,
    val type: String? = null,
    val district: String? = null,
    val taluk: String? = null,
    val village: String? = null,
    val address: String? = null,
    val totalCapacitySacks: Int? = null,
    val availableCapacitySacks: Int? = null,
    val pricePerSack: Double? = null,
    val contactName: String? = null,
    val contactPhone: String? = null,
    val ownerType: String? = null,
    val description: String? = null,
    val isActive: Boolean? = null
)

// Facility Response
@Serializable
data class FacilityResponse(
    val id: String,
    val name: String,
    val type: String,
    val district: String,
    val taluk: String,
    val village: String,
    val address: String?,
    val totalCapacitySacks: Int,
    val availableCapacitySacks: Int,
    val pricePerSack: Double?,
    val contactName: String?,
    val contactPhone: String?,
    val ownerType: String?,
    val description: String?,
    val isActive: Boolean
)

// Facility History
@Serializable
data class FacilityHistoryResponse(
    val id: String,
    val facilityId: String,
    val action: String,
    val changedFields: Map<String, String>?,
    val oldValues: Map<String, String>?,
    val newValues: Map<String, String>?,
    val changedAt: String,
    val changedBy: String?
)

