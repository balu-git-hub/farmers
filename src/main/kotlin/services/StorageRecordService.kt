package com.payir.services

import com.payir.dto.*
import com.payir.models.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class StorageRecordService {
    
    suspend fun createStorageRecord(
        farmerId: String,
        request: CreateStorageRecordRequest,
        createdBy: String
    ): StorageRecordResponse = newSuspendedTransaction(Dispatchers.IO) {
        val recordId = UUID.randomUUID()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        
        StorageRecords.insert {
            it[id] = recordId
            it[StorageRecords.farmerId] = UUID.fromString(farmerId)
            it[StorageRecords.bookingId] = request.bookingId?.let { UUID.fromString(it) }
            it[StorageRecords.cropType] = request.cropType
            it[StorageRecords.quantitySacks] = request.quantitySacks
            it[StorageRecords.storageType] = request.storageType
            it[StorageRecords.moisturePercent] = request.moisturePercent?.toBigDecimal()
            it[StorageRecords.storageLocationDesc] = request.storageLocationDesc
            it[StorageRecords.facilityId] = request.facilityId?.let { UUID.fromString(it) }
            it[StorageRecords.storedDate] = request.storedDate?.let { LocalDate.parse(it, formatter) }
            it[StorageRecords.createdBy] = UUID.fromString(createdBy)
            it[StorageRecords.updatedBy] = UUID.fromString(createdBy)
        }
        
        // Create history entry
        StorageRecordsHistory.insert {
            it[StorageRecordsHistory.storageRecordId] = recordId
            it[StorageRecordsHistory.action] = "CREATED"
            it[StorageRecordsHistory.changedBy] = UUID.fromString(createdBy)
        }
        
        StorageRecordResponse(
            id = recordId.toString(),
            farmerId = farmerId,
            bookingId = request.bookingId,
            cropType = request.cropType,
            quantitySacks = request.quantitySacks,
            storageType = request.storageType,
            moisturePercent = request.moisturePercent,
            storageLocationDesc = request.storageLocationDesc,
            facilityId = request.facilityId,
            storedDate = request.storedDate,
            createdAt = java.time.Clock.systemUTC().instant().toString(),
            updatedAt = java.time.Clock.systemUTC().instant().toString()
        )
    }
    
    suspend fun getStorageRecordsByFarmer(farmerId: String): List<StorageRecordResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            StorageRecords.selectAll().where { StorageRecords.farmerId eq UUID.fromString(farmerId) }
                .map {
                    StorageRecordResponse(
                        id = it[StorageRecords.id].toString(),
                        farmerId = it[StorageRecords.farmerId].toString(),
                        bookingId = it[StorageRecords.bookingId]?.toString(),
                        cropType = it[StorageRecords.cropType],
                        quantitySacks = it[StorageRecords.quantitySacks],
                        storageType = it[StorageRecords.storageType],
                        moisturePercent = it[StorageRecords.moisturePercent]?.toDouble(),
                        storageLocationDesc = it[StorageRecords.storageLocationDesc],
                        facilityId = it[StorageRecords.facilityId]?.toString(),
                        storedDate = it[StorageRecords.storedDate]?.toString(),
                        createdAt = it[StorageRecords.createdAt].toString(),
                        updatedAt = it[StorageRecords.updatedAt].toString()
                    )
                }
        }
    
    suspend fun getStorageRecordById(recordId: String): StorageRecordResponse? = 
        newSuspendedTransaction(Dispatchers.IO) {
            val record = StorageRecords.selectAll().where { StorageRecords.id eq UUID.fromString(recordId) }
                .singleOrNull()
            
            record?.let {
                StorageRecordResponse(
                    id = it[StorageRecords.id].toString(),
                    farmerId = it[StorageRecords.farmerId].toString(),
                    bookingId = it[StorageRecords.bookingId]?.toString(),
                    cropType = it[StorageRecords.cropType],
                    quantitySacks = it[StorageRecords.quantitySacks],
                    storageType = it[StorageRecords.storageType],
                    moisturePercent = it[StorageRecords.moisturePercent]?.toDouble(),
                    storageLocationDesc = it[StorageRecords.storageLocationDesc],
                    facilityId = it[StorageRecords.facilityId]?.toString(),
                    storedDate = it[StorageRecords.storedDate]?.toString(),
                    createdAt = it[StorageRecords.createdAt].toString(),
                    updatedAt = it[StorageRecords.updatedAt].toString()
                )
            }
        }
    
    suspend fun getStorageRecordHistory(recordId: String): List<StorageRecordHistoryResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            StorageRecordsHistory.selectAll().where { 
                StorageRecordsHistory.storageRecordId eq UUID.fromString(recordId) 
            }
                .map {
                    StorageRecordHistoryResponse(
                        id = it[StorageRecordsHistory.id].toString(),
                        storageRecordId = it[StorageRecordsHistory.storageRecordId].toString(),
                        action = it[StorageRecordsHistory.action],
                        changedFields = null, // TODO: Parse JSON
                        oldValues = null,
                        newValues = null,
                        changedAt = it[StorageRecordsHistory.changedAt].toString(),
                        changedBy = it[StorageRecordsHistory.changedBy]?.toString()
                    )
                }
        }
}

