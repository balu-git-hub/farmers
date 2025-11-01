package com.payir.dto

import kotlinx.serialization.Serializable

// Farmer Registration
@Serializable
data class FarmerRegisterRequest(
    val username: String,
    val password: String,
    val name: String,
    val aadhaarNumber: String? = null,
    val district: String,
    val taluk: String,
    val village: String,
    val address: String? = null,
    val email: String? = null,
    val phone: String? = null
)

@Serializable
data class FarmerRegisterResponse(
    val farmerId: String,
    val userId: String,
    val verificationStatus: String,
    val message: String
)

// Farmer Details
@Serializable
data class FarmerResponse(
    val id: String,
    val userId: String,
    val name: String,
    val aadhaarNumber: String?,
    val district: String,
    val taluk: String,
    val village: String,
    val address: String?,
    val landRegistrationNumber: String?,
    val verificationStatus: String,
    val verifiedAt: String?,
    val rejectionReason: String?
)

// Crop Planting
@Serializable
data class CropPlantingRequest(
    val cropType: String,
    val season: String, // KHARIF, RABI, ZAID
    val year: Int,
    val areaMeasurement: Double,
    val measurementUnit: String = "ACRES", // ACRES, HECTARES
    val district: String,
    val taluk: String,
    val village: String,
    val plotDetails: String? = null,
    val plantingDate: String? = null,
    val expectedHarvestDate: String? = null
)

@Serializable
data class CropPlantingResponse(
    val id: String,
    val farmerId: String,
    val cropType: String,
    val season: String,
    val year: Int,
    val areaMeasurement: Double,
    val measurementUnit: String,
    val district: String,
    val taluk: String,
    val village: String,
    val plotDetails: String?,
    val plantingDate: String?,
    val expectedHarvestDate: String?,
    val status: String
)

// Document Upload
@Serializable
data class DocumentUploadResponse(
    val documentId: String,
    val filePath: String,
    val documentType: String,
    val verificationStatus: String
)

