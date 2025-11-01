package com.payir.services

import com.payir.dto.*
import com.payir.models.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class AdminService {
    
    suspend fun getAllBookings(): List<BookingResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            Bookings.selectAll()
                .map {
                    BookingResponse(
                        id = it[Bookings.id].toString(),
                        farmerId = it[Bookings.farmerId].toString(),
                        facilityId = it[Bookings.facilityId].toString(),
                        cropType = it[Bookings.cropType],
                        quantitySacks = it[Bookings.quantitySacks],
                        status = it[Bookings.status],
                        pricePerSack = it[Bookings.pricePerSack]?.toDouble(),
                        priceTotal = it[Bookings.priceTotal]?.toDouble(),
                        startDate = it[Bookings.startDate].toString(),
                        endDate = it[Bookings.endDate]?.toString(),
                        notes = it[Bookings.notes],
                        createdAt = it[Bookings.createdAt].toString(),
                        updatedAt = it[Bookings.updatedAt].toString()
                    )
                }
        }
    
    suspend fun getAllStorageRecords(): List<StorageRecordResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            StorageRecords.selectAll()
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
}

