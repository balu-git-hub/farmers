package com.payir.services

import com.payir.dto.*
import com.payir.models.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class SyncService {
    
    suspend fun getSeedPackage(version: Int?): SeedPackageResponse? = 
        newSuspendedTransaction(Dispatchers.IO) {
            // Get current seed version
            val currentSeed = SeedMetadata.selectAll()
                .orderBy(SeedMetadata.version to SortOrder.DESC)
                .limit(1)
                .singleOrNull()
            
            val currentVersion = currentSeed?.get(SeedMetadata.version) ?: 0
            
            // If client version matches, return null (204)
            if (version != null && version == currentVersion) {
                return@newSuspendedTransaction null
            }
            
            // Get facilities (minimal fields for seed)
            val facilities = Facilities.selectAll().where { Facilities.isActive eq true }
                .map {
                    FacilitySeedItem(
                        id = it[Facilities.id].toString(),
                        name = it[Facilities.name],
                        type = it[Facilities.type],
                        district = it[Facilities.district],
                        taluk = it[Facilities.taluk],
                        village = it[Facilities.village],
                        totalCapacitySacks = it[Facilities.totalCapacitySacks],
                        availableCapacitySacks = it[Facilities.availableCapacitySacks],
                        pricePerSack = it[Facilities.pricePerSack]?.toDouble()
                    )
                }
            
            // Get locations
            val districts = Districts.selectAll()
                .map {
                    DistrictResponse(
                        id = it[Districts.id].toString(),
                        name = it[Districts.name],
                        state = it[Districts.state],
                        code = it[Districts.code]
                    )
                }
            
            val taluks = Taluks.selectAll()
                .map {
                    TalukResponse(
                        id = it[Taluks.id].toString(),
                        districtId = it[Taluks.districtId].toString(),
                        name = it[Taluks.name],
                        code = it[Taluks.code]
                    )
                }
            
            val villages = Villages.selectAll()
                .map {
                    VillageResponse(
                        id = it[Villages.id].toString(),
                        talukId = it[Villages.talukId].toString(),
                        name = it[Villages.name],
                        code = it[Villages.code]
                    )
                }
            
            SeedPackageResponse(
                seedVersion = currentVersion,
                facilities = facilities,
                districts = districts,
                taluks = taluks,
                villages = villages
            )
        }
    
    suspend fun syncChanges(
        userId: String,
        request: SyncChangesRequest
    ): SyncChangesResponse = newSuspendedTransaction(Dispatchers.IO) {
        val conflicts = mutableListOf<SyncConflict>()
        
        // Process bookings
        request.changes.bookings.forEach { bookingRequest ->
            try {
                // This would call BookingService, but for now we'll just note it
                // In a full implementation, you'd validate each booking and handle conflicts
            } catch (e: Exception) {
                conflicts.add(
                    SyncConflict(
                        entityType = "BOOKING",
                        entityId = "",
                        conflictType = "VALIDATION_ERROR",
                        message = e.message ?: "Unknown error"
                    )
                )
            }
        }
        
        // Process storage records
        request.changes.storageRecords.forEach { storageRequest ->
            try {
                // Similar validation and conflict handling
            } catch (e: Exception) {
                conflicts.add(
                    SyncConflict(
                        entityType = "STORAGE_RECORD",
                        entityId = "",
                        conflictType = "VALIDATION_ERROR",
                        message = e.message ?: "Unknown error"
                    )
                )
            }
        }
        
        // Update device sync state
        val deviceSync = DeviceSyncState.selectAll().where { 
            (DeviceSyncState.userId eq java.util.UUID.fromString(userId)) and 
            (DeviceSyncState.deviceId eq request.deviceId)
        }.singleOrNull()
        
        if (deviceSync != null) {
            DeviceSyncState.update({ 
                (DeviceSyncState.userId eq java.util.UUID.fromString(userId)) and 
                (DeviceSyncState.deviceId eq request.deviceId)
            }) {
                it[DeviceSyncState.lastSyncAt] = java.time.Clock.systemUTC().instant()
            }
        } else {
            DeviceSyncState.insert {
                it[DeviceSyncState.userId] = java.util.UUID.fromString(userId)
                it[DeviceSyncState.deviceId] = request.deviceId
                it[DeviceSyncState.lastSyncAt] = java.time.Clock.systemUTC().instant()
            }
        }
        
        SyncChangesResponse(
            success = conflicts.isEmpty(),
            conflicts = if (conflicts.isNotEmpty()) conflicts else null,
            resolutionSuggestions = if (conflicts.isNotEmpty()) {
                listOf("Please review conflicts and resubmit", "Contact support if issues persist")
            } else null
        )
    }
}

