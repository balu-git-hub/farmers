package com.payir.dto

import kotlinx.serialization.Serializable

// Farmer Verification
@Serializable
data class FarmerApprovalRequest(
    val rejectionReason: String? = null
)

@Serializable
data class FarmerVerificationResponse(
    val status: String,
    val verifiedAt: String? = null,
    val rejectionReason: String? = null
)

// Document Verification
@Serializable
data class DocumentVerificationRequest(
    val verificationStatus: String, // VERIFIED, REJECTED
    val rejectionReason: String? = null
)

@Serializable
data class DocumentVerificationResponse(
    val status: String,
    val verifiedAt: String? = null,
    val rejectionReason: String? = null
)

// Pending Farmers Response
@Serializable
data class PendingFarmerResponse(
    val id: String,
    val userId: String,
    val name: String,
    val aadhaarNumber: String?,
    val district: String,
    val taluk: String,
    val village: String,
    val verificationStatus: String,
    val createdAt: String
)

// Pending Documents Response
@Serializable
data class PendingDocumentResponse(
    val id: String,
    val farmerId: String,
    val documentType: String,
    val documentName: String,
    val filePath: String,
    val verificationStatus: String,
    val createdAt: String
)

