package com.payir.dto

import kotlinx.serialization.Serializable

// Booking Request
@Serializable
data class CheckAvailabilityRequest(
    val facilityId: String,
    val quantitySacks: Int
)

@Serializable
data class CheckAvailabilityResponse(
    val available: Boolean,
    val availCount: Int,
    val message: String? = null
)

@Serializable
data class CreateBookingRequest(
    val facilityId: String,
    val cropType: String,
    val quantitySacks: Int,
    val startDate: String,
    val endDate: String? = null,
    val notes: String? = null
)

@Serializable
data class CreateBookingResponse(
    val bookingId: String,
    val status: String,
    val availableAfterBooking: Int? = null
)

@Serializable
data class BookingConflictResponse(
    val error: String,
    val available: Int,
    val message: String
)

// Booking Response
@Serializable
data class BookingResponse(
    val id: String,
    val farmerId: String,
    val facilityId: String,
    val cropType: String,
    val quantitySacks: Int,
    val status: String,
    val pricePerSack: Double?,
    val priceTotal: Double?,
    val startDate: String,
    val endDate: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
)

// Booking History
@Serializable
data class BookingHistoryResponse(
    val id: String,
    val bookingId: String,
    val action: String,
    val oldStatus: String?,
    val newStatus: String?,
    val changedFields: Map<String, String>?,
    val changedAt: String,
    val changedBy: String?
)

