package com.payir.dto

import kotlinx.serialization.Serializable

// Storage Record Request
@Serializable
data class CreateStorageRecordRequest(
    val cropType: String,
    val quantitySacks: Int,
    val moisturePercent: Double? = null,
    val storageType: String? = null, // HDPE_BAGS, SILO, etc.
    val facilityId: String? = null,
    val bookingId: String? = null,
    val storageLocationDesc: String? = null,
    val storedDate: String? = null
)

// Storage Record Response
@Serializable
data class StorageRecordResponse(
    val id: String,
    val farmerId: String,
    val bookingId: String?,
    val cropType: String,
    val quantitySacks: Int,
    val storageType: String?,
    val moisturePercent: Double?,
    val storageLocationDesc: String?,
    val facilityId: String?,
    val storedDate: String?,
    val createdAt: String,
    val updatedAt: String
)

// Storage Record History
@Serializable
data class StorageRecordHistoryResponse(
    val id: String,
    val storageRecordId: String,
    val action: String,
    val changedFields: Map<String, String>?,
    val oldValues: Map<String, String>?,
    val newValues: Map<String, String>?,
    val changedAt: String,
    val changedBy: String?
)

