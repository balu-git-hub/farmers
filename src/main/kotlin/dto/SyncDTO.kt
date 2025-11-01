package com.payir.dto

import kotlinx.serialization.Serializable

// Seed Package Response
@Serializable
data class SeedPackageResponse(
    val seedVersion: Int,
    val facilities: List<FacilitySeedItem>,
    val districts: List<DistrictResponse>,
    val taluks: List<TalukResponse>,
    val villages: List<VillageResponse>
)

@Serializable
data class FacilitySeedItem(
    val id: String,
    val name: String,
    val type: String,
    val district: String,
    val taluk: String,
    val village: String,
    val totalCapacitySacks: Int,
    val availableCapacitySacks: Int,
    val pricePerSack: Double?
)

// Sync Request
@Serializable
data class SyncChangesRequest(
    val deviceId: String,
    val userId: String? = null, // Optional - if not provided, uses "anonymous"
    val lastSyncAt: String?,
    val changes: SyncChanges
)

@Serializable
data class SyncChanges(
    val bookings: List<CreateBookingRequest> = emptyList(),
    val storageRecords: List<CreateStorageRecordRequest> = emptyList()
)

// Sync Response
@Serializable
data class SyncChangesResponse(
    val success: Boolean,
    val conflicts: List<SyncConflict>? = null,
    val resolutionSuggestions: List<String>? = null
)

@Serializable
data class SyncConflict(
    val entityType: String,
    val entityId: String,
    val conflictType: String,
    val message: String
)

