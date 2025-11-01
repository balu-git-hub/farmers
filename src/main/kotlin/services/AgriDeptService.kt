package com.payir.services

import com.payir.dto.*
import com.payir.models.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Clock
import java.util.*

class AgriDeptService {
    
    suspend fun getPendingFarmers(): List<PendingFarmerResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            Farmers.selectAll().where { Farmers.verificationStatus eq "PENDING" }
                .map {
                    PendingFarmerResponse(
                        id = it[Farmers.id].toString(),
                        userId = it[Farmers.userId].toString(),
                        name = it[Farmers.name],
                        aadhaarNumber = it[Farmers.aadhaarNumber],
                        district = it[Farmers.district],
                        taluk = it[Farmers.taluk],
                        village = it[Farmers.village],
                        verificationStatus = it[Farmers.verificationStatus],
                        createdAt = it[Farmers.createdAt].toString()
                    )
                }
        }
    
    suspend fun approveFarmer(
        farmerId: String,
        agriDeptPersonnelId: String
    ): FarmerVerificationResponse = newSuspendedTransaction(Dispatchers.IO) {
        val farmer = Farmers.selectAll().where { Farmers.id eq UUID.fromString(farmerId) }
            .singleOrNull()
            ?: throw IllegalArgumentException("Farmer not found")
        
        if (farmer[Farmers.verificationStatus] != "PENDING") {
            throw IllegalArgumentException("Farmer is not in PENDING status")
        }
        
        val verifiedAt = Clock.systemUTC().instant()
        
        Farmers.update({ Farmers.id eq UUID.fromString(farmerId) }) {
            it[Farmers.verificationStatus] = "APPROVED"
            it[Farmers.verifiedBy] = UUID.fromString(agriDeptPersonnelId)
            it[Farmers.verifiedAt] = verifiedAt
            it[Farmers.updatedBy] = UUID.fromString(agriDeptPersonnelId)
        }
        
        FarmerVerificationResponse(
            status = "APPROVED",
            verifiedAt = verifiedAt.toString()
        )
    }
    
    suspend fun rejectFarmer(
        farmerId: String,
        rejectionReason: String,
        agriDeptPersonnelId: String
    ): FarmerVerificationResponse = newSuspendedTransaction(Dispatchers.IO) {
        val farmer = Farmers.selectAll().where { Farmers.id eq UUID.fromString(farmerId) }
            .singleOrNull()
            ?: throw IllegalArgumentException("Farmer not found")
        
        if (farmer[Farmers.verificationStatus] != "PENDING") {
            throw IllegalArgumentException("Farmer is not in PENDING status")
        }
        
        val verifiedAt = Clock.systemUTC().instant()
        
        Farmers.update({ Farmers.id eq UUID.fromString(farmerId) }) {
            it[Farmers.verificationStatus] = "REJECTED"
            it[Farmers.verifiedBy] = UUID.fromString(agriDeptPersonnelId)
            it[Farmers.verifiedAt] = verifiedAt
            it[Farmers.rejectionReason] = rejectionReason
            it[Farmers.updatedBy] = UUID.fromString(agriDeptPersonnelId)
        }
        
        FarmerVerificationResponse(
            status = "REJECTED",
            verifiedAt = verifiedAt.toString(),
            rejectionReason = rejectionReason
        )
    }
    
    suspend fun getPendingDocuments(): List<PendingDocumentResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            FarmerDocuments.selectAll().where { FarmerDocuments.verificationStatus eq "PENDING" }
                .map {
                    PendingDocumentResponse(
                        id = it[FarmerDocuments.id].toString(),
                        farmerId = it[FarmerDocuments.farmerId].toString(),
                        documentType = it[FarmerDocuments.documentType],
                        documentName = it[FarmerDocuments.documentName],
                        filePath = it[FarmerDocuments.filePath],
                        verificationStatus = it[FarmerDocuments.verificationStatus],
                        createdAt = it[FarmerDocuments.createdAt].toString()
                    )
                }
        }
    
    suspend fun verifyDocument(
        documentId: String,
        request: DocumentVerificationRequest,
        agriDeptPersonnelId: String
    ): DocumentVerificationResponse = newSuspendedTransaction(Dispatchers.IO) {
        val document = FarmerDocuments.selectAll().where { 
            FarmerDocuments.id eq UUID.fromString(documentId) 
        }
            .singleOrNull()
            ?: throw IllegalArgumentException("Document not found")
        
        if (document[FarmerDocuments.verificationStatus] != "PENDING") {
            throw IllegalArgumentException("Document is not in PENDING status")
        }
        
        val verifiedAt = Clock.systemUTC().instant()
        
        FarmerDocuments.update({ FarmerDocuments.id eq UUID.fromString(documentId) }) {
            it[FarmerDocuments.verificationStatus] = request.verificationStatus
            it[FarmerDocuments.verifiedBy] = UUID.fromString(agriDeptPersonnelId)
            it[FarmerDocuments.verifiedAt] = verifiedAt
            if (request.verificationStatus == "REJECTED") {
                it[FarmerDocuments.rejectionReason] = request.rejectionReason
            }
            it[FarmerDocuments.updatedBy] = UUID.fromString(agriDeptPersonnelId)
        }
        
        DocumentVerificationResponse(
            status = request.verificationStatus,
            verifiedAt = verifiedAt.toString(),
            rejectionReason = if (request.verificationStatus == "REJECTED") {
                request.rejectionReason
            } else null
        )
    }
}

