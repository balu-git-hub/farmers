package com.payir.services

import com.payir.dto.*
import com.payir.models.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class FacilityService {
    
    suspend fun getFacilities(
        district: String? = null,
        taluk: String? = null,
        village: String? = null,
        type: String? = null
    ): List<FacilityResponse> = newSuspendedTransaction(Dispatchers.IO) {
        var query = Facilities.selectAll().where { Facilities.isActive eq true }
        
        if (district != null) {
            query = query.andWhere { Facilities.district eq district }
        }
        if (taluk != null) {
            query = query.andWhere { Facilities.taluk eq taluk }
        }
        if (village != null) {
            query = query.andWhere { Facilities.village eq village }
        }
        if (type != null) {
            query = query.andWhere { Facilities.type eq type }
        }
        
        query.map {
            FacilityResponse(
                id = it[Facilities.id].toString(),
                name = it[Facilities.name],
                type = it[Facilities.type],
                district = it[Facilities.district],
                taluk = it[Facilities.taluk],
                village = it[Facilities.village],
                address = it[Facilities.address],
                totalCapacitySacks = it[Facilities.totalCapacitySacks],
                availableCapacitySacks = it[Facilities.availableCapacitySacks],
                pricePerSack = it[Facilities.pricePerSack]?.toDouble(),
                contactName = it[Facilities.contactName],
                contactPhone = it[Facilities.contactPhone],
                ownerType = it[Facilities.ownerType],
                description = it[Facilities.description],
                isActive = it[Facilities.isActive]
            )
        }
    }
    
    suspend fun getFacilityById(facilityId: String): FacilityResponse? = 
        newSuspendedTransaction(Dispatchers.IO) {
            val facility = Facilities.selectAll().where { Facilities.id eq UUID.fromString(facilityId) }
                .singleOrNull()
            
            facility?.let {
                FacilityResponse(
                    id = it[Facilities.id].toString(),
                    name = it[Facilities.name],
                    type = it[Facilities.type],
                    district = it[Facilities.district],
                    taluk = it[Facilities.taluk],
                    village = it[Facilities.village],
                    address = it[Facilities.address],
                    totalCapacitySacks = it[Facilities.totalCapacitySacks],
                    availableCapacitySacks = it[Facilities.availableCapacitySacks],
                    pricePerSack = it[Facilities.pricePerSack]?.toDouble(),
                    contactName = it[Facilities.contactName],
                    contactPhone = it[Facilities.contactPhone],
                    ownerType = it[Facilities.ownerType],
                    description = it[Facilities.description],
                    isActive = it[Facilities.isActive]
                )
            }
        }
    
    suspend fun createFacility(request: CreateFacilityRequest, createdBy: String): FacilityResponse = 
        newSuspendedTransaction(Dispatchers.IO) {
            val facilityId = UUID.randomUUID()
            val userId = UUID.fromString(createdBy)
            
            Facilities.insert {
                it[id] = facilityId
                it[Facilities.name] = request.name
                it[Facilities.type] = request.type
                it[Facilities.district] = request.district
                it[Facilities.taluk] = request.taluk
                it[Facilities.village] = request.village
                it[Facilities.address] = request.address
                it[Facilities.totalCapacitySacks] = request.totalCapacitySacks
                it[Facilities.availableCapacitySacks] = request.availableCapacitySacks
                it[Facilities.pricePerSack] = request.pricePerSack?.toBigDecimal()
                it[Facilities.contactName] = request.contactName
                it[Facilities.contactPhone] = request.contactPhone
                it[Facilities.ownerType] = request.ownerType
                it[Facilities.description] = request.description
                it[Facilities.isActive] = true
                it[Facilities.createdBy] = userId
                it[Facilities.updatedBy] = userId
            }
            
            // Create history entry
            FacilitiesHistory.insert {
                it[FacilitiesHistory.facilityId] = facilityId
                it[FacilitiesHistory.action] = "CREATED"
                it[FacilitiesHistory.changedBy] = userId
            }
            
            FacilityResponse(
                id = facilityId.toString(),
                name = request.name,
                type = request.type,
                district = request.district,
                taluk = request.taluk,
                village = request.village,
                address = request.address,
                totalCapacitySacks = request.totalCapacitySacks,
                availableCapacitySacks = request.availableCapacitySacks,
                pricePerSack = request.pricePerSack,
                contactName = request.contactName,
                contactPhone = request.contactPhone,
                ownerType = request.ownerType,
                description = request.description,
                isActive = true
            )
        }
    
    suspend fun updateFacility(
        facilityId: String,
        request: UpdateFacilityRequest,
        updatedBy: String
    ): FacilityResponse = newSuspendedTransaction(Dispatchers.IO) {
        val facility = Facilities.selectAll().where { Facilities.id eq UUID.fromString(facilityId) }
            .singleOrNull()
            ?: throw IllegalArgumentException("Facility not found")
        
        val oldValues = mapOf(
            "name" to facility[Facilities.name],
            "availableCapacitySacks" to facility[Facilities.availableCapacitySacks].toString(),
            "pricePerSack" to facility[Facilities.pricePerSack]?.toString()
        )
        
        Facilities.update({ Facilities.id eq UUID.fromString(facilityId) }) { update ->
            request.name?.let { update[Facilities.name] = it }
            request.type?.let { update[Facilities.type] = it }
            request.district?.let { update[Facilities.district] = it }
            request.taluk?.let { update[Facilities.taluk] = it }
            request.village?.let { update[Facilities.village] = it }
            request.address?.let { update[Facilities.address] = it }
            request.totalCapacitySacks?.let { update[Facilities.totalCapacitySacks] = it }
            request.availableCapacitySacks?.let { update[Facilities.availableCapacitySacks] = it }
            request.pricePerSack?.let { update[Facilities.pricePerSack] = it.toBigDecimal() }
            request.contactName?.let { update[Facilities.contactName] = it }
            request.contactPhone?.let { update[Facilities.contactPhone] = it }
            request.ownerType?.let { update[Facilities.ownerType] = it }
            request.description?.let { update[Facilities.description] = it }
            request.isActive?.let { update[Facilities.isActive] = it }
            update[Facilities.updatedBy] = UUID.fromString(updatedBy)
        }
        
        val updated = Facilities.selectAll().where { Facilities.id eq UUID.fromString(facilityId) }.single()
        
        // Create history entry
        FacilitiesHistory.insert {
            it[FacilitiesHistory.facilityId] = UUID.fromString(facilityId)
            it[FacilitiesHistory.action] = "UPDATED"
            it[FacilitiesHistory.oldValues] = oldValues.toString() // Simplified
            it[FacilitiesHistory.changedBy] = UUID.fromString(updatedBy)
        }
        
        FacilityResponse(
            id = updated[Facilities.id].toString(),
            name = updated[Facilities.name],
            type = updated[Facilities.type],
            district = updated[Facilities.district],
            taluk = updated[Facilities.taluk],
            village = updated[Facilities.village],
            address = updated[Facilities.address],
            totalCapacitySacks = updated[Facilities.totalCapacitySacks],
            availableCapacitySacks = updated[Facilities.availableCapacitySacks],
            pricePerSack = updated[Facilities.pricePerSack]?.toDouble(),
            contactName = updated[Facilities.contactName],
            contactPhone = updated[Facilities.contactPhone],
            ownerType = updated[Facilities.ownerType],
            description = updated[Facilities.description],
            isActive = updated[Facilities.isActive]
        )
    }
    
    suspend fun getFacilityHistory(facilityId: String): List<FacilityHistoryResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            FacilitiesHistory.selectAll().where { FacilitiesHistory.facilityId eq UUID.fromString(facilityId) }
                .map {
                    FacilityHistoryResponse(
                        id = it[FacilitiesHistory.id].toString(),
                        facilityId = it[FacilitiesHistory.facilityId].toString(),
                        action = it[FacilitiesHistory.action],
                        changedFields = null, // TODO: Parse JSON
                        oldValues = null,
                        newValues = null,
                        changedAt = it[FacilitiesHistory.changedAt].toString(),
                        changedBy = it[FacilitiesHistory.changedBy]?.toString()
                    )
                }
        }
}

