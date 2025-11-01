package com.payir.services

import com.payir.dto.*
import com.payir.models.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class BookingService {
    
    suspend fun checkAvailability(request: CheckAvailabilityRequest): CheckAvailabilityResponse = 
        newSuspendedTransaction(Dispatchers.IO) {
            val facility = Facilities.selectAll().where { Facilities.id eq UUID.fromString(request.facilityId) }
                .singleOrNull()
                ?: throw IllegalArgumentException("Facility not found")
            
            val availableCapacity = facility[Facilities.availableCapacitySacks]
            val isAvailable = availableCapacity >= request.quantitySacks
            
            CheckAvailabilityResponse(
                available = isAvailable,
                availCount = availableCapacity,
                message = if (!isAvailable) {
                    "Only $availableCapacity sacks are available at selected facility."
                } else null
            )
        }
    
    suspend fun createBooking(
        farmerId: String,
        request: CreateBookingRequest,
        createdBy: String
    ): CreateBookingResponse = newSuspendedTransaction(Dispatchers.IO) {
        // Check availability and update in a transaction with lock
        val facilityId = UUID.fromString(request.facilityId)
        
        // Lock the facility row for update
        val facility = Facilities.selectAll().where {
            Facilities.id eq facilityId
        }.forUpdate().singleOrNull()
            ?: throw IllegalArgumentException("Facility not found")
        
        val availableCapacity = facility[Facilities.availableCapacitySacks]
        
        if (availableCapacity < request.quantitySacks) {
            throw IllegalArgumentException("Insufficient capacity. Available: $availableCapacity, Requested: ${request.quantitySacks}")
        }
        
        // Create booking
        val bookingId = UUID.randomUUID()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val pricePerSack = facility[Facilities.pricePerSack]?.toDouble()
        val priceTotal = pricePerSack?.times(request.quantitySacks)
        
        Bookings.insert {
            it[id] = bookingId
            it[Bookings.farmerId] = UUID.fromString(farmerId)
            it[Bookings.facilityId] = facilityId
            it[Bookings.cropType] = request.cropType
            it[Bookings.quantitySacks] = request.quantitySacks
            it[Bookings.status] = "CONFIRMED"
            it[Bookings.pricePerSack] = facility[Facilities.pricePerSack]
            it[Bookings.priceTotal] = priceTotal?.toBigDecimal()
            it[Bookings.startDate] = LocalDate.parse(request.startDate, formatter)
            it[Bookings.endDate] = request.endDate?.let { LocalDate.parse(it, formatter) }
            it[Bookings.notes] = request.notes
            it[Bookings.createdBy] = UUID.fromString(createdBy)
            it[Bookings.updatedBy] = UUID.fromString(createdBy)
        }
        
        // Decrement available capacity
        Facilities.update({ Facilities.id eq facilityId }) {
            it[availableCapacitySacks] = availableCapacity - request.quantitySacks
        }
        
        // Create history entry
        BookingsHistory.insert {
            it[BookingsHistory.bookingId] = bookingId
            it[BookingsHistory.action] = "CREATED"
            it[BookingsHistory.newStatus] = "CONFIRMED"
            it[BookingsHistory.updatedBy] = UUID.fromString(createdBy)
        }
        
        val newAvailable = availableCapacity - request.quantitySacks
        
        CreateBookingResponse(
            bookingId = bookingId.toString(),
            status = "CONFIRMED",
            availableAfterBooking = newAvailable
        )
    }
    
    suspend fun getBookingsByFarmer(farmerId: String): List<BookingResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            Bookings.selectAll().where { Bookings.farmerId eq UUID.fromString(farmerId) }
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
    
    suspend fun getBookingById(bookingId: String): BookingResponse? = 
        newSuspendedTransaction(Dispatchers.IO) {
            val booking = Bookings.selectAll().where { Bookings.id eq UUID.fromString(bookingId) }
                .singleOrNull()
            
            booking?.let {
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
    
    suspend fun cancelBooking(bookingId: String, changedBy: String) = 
        newSuspendedTransaction(Dispatchers.IO) {
            val booking = Bookings.selectAll().where { Bookings.id eq UUID.fromString(bookingId) }
                .singleOrNull()
                ?: throw IllegalArgumentException("Booking not found")
            
            if (booking[Bookings.status] == "CANCELLED") {
                throw IllegalArgumentException("Booking is already cancelled")
            }
            
            val oldStatus = booking[Bookings.status]
            val facilityId = booking[Bookings.facilityId]
            val quantitySacks = booking[Bookings.quantitySacks]
            
            // Update booking status
            Bookings.update({ Bookings.id eq UUID.fromString(bookingId) }) {
                it[status] = "CANCELLED"
                it[updatedBy] = UUID.fromString(changedBy)
            }
            
            // Restore capacity if booking was confirmed
            if (oldStatus == "CONFIRMED") {
                val facility = Facilities.selectAll().where { Facilities.id eq facilityId }.single()
                val currentCapacity = facility[Facilities.availableCapacitySacks]
                Facilities.update({ Facilities.id eq facilityId }) {
                    it[availableCapacitySacks] = currentCapacity + quantitySacks
                }
            }
            
            // Create history entry
            BookingsHistory.insert {
                it[BookingsHistory.bookingId] = UUID.fromString(bookingId)
                it[BookingsHistory.action] = "STATUS_CHANGED"
                it[BookingsHistory.oldStatus] = oldStatus
                it[BookingsHistory.newStatus] = "CANCELLED"
                it[BookingsHistory.updatedBy] = UUID.fromString(changedBy)
            }
        }
    
    suspend fun getBookingHistory(bookingId: String): List<BookingHistoryResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            BookingsHistory.selectAll().where { BookingsHistory.bookingId eq UUID.fromString(bookingId) }
                .map {
                    BookingHistoryResponse(
                        id = it[BookingsHistory.id].toString(),
                        bookingId = it[BookingsHistory.bookingId].toString(),
                        action = it[BookingsHistory.action],
                        oldStatus = it[BookingsHistory.oldStatus],
                        newStatus = it[BookingsHistory.newStatus],
                        changedFields = null, // TODO: Parse JSON if needed
                        changedAt = it[BookingsHistory.changedAt].toString(),
                        changedBy = it[BookingsHistory.updatedBy]?.toString()
                    )
                }
        }
}

