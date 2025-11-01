package com.payir.services

import com.payir.dto.*
import com.payir.models.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class FarmerService {
    
    suspend fun registerFarmer(request: FarmerRegisterRequest): FarmerRegisterResponse = 
        newSuspendedTransaction(Dispatchers.IO) {
            // Check if username exists
            val existingUser = Users.selectAll().where { Users.username eq request.username }.singleOrNull()
            if (existingUser != null) {
                throw IllegalArgumentException("Username already exists")
            }
            
            // Create user first
            val userId = UUID.randomUUID()
            val passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())
            
            Users.insert {
                it[id] = userId
                it[Users.username] = request.username
                it[Users.passwordHash] = passwordHash
                it[Users.role] = "FARMER"
                it[Users.email] = request.email
                it[Users.phone] = request.phone
                it[Users.isActive] = true
                it[Users.createdBy] = userId
                it[Users.updatedBy] = userId
            }
            
            // Create farmer record
            val farmerId = UUID.randomUUID()
            Farmers.insert {
                it[id] = farmerId
                it[Farmers.userId] = userId
                it[Farmers.name] = request.name
                it[Farmers.aadhaarNumber] = request.aadhaarNumber
                it[Farmers.district] = request.district
                it[Farmers.taluk] = request.taluk
                it[Farmers.village] = request.village
                it[Farmers.address] = request.address
                it[Farmers.verificationStatus] = "PENDING"
                it[Farmers.createdBy] = userId
                it[Farmers.updatedBy] = userId
            }
            
            FarmerRegisterResponse(
                farmerId = farmerId.toString(),
                userId = userId.toString(),
                verificationStatus = "PENDING",
                message = "Registration successful. Please upload documents for verification."
            )
        }
    
    suspend fun getFarmerById(farmerId: String): FarmerResponse? = newSuspendedTransaction(Dispatchers.IO) {
        val farmer = Farmers.selectAll().where { Farmers.id eq UUID.fromString(farmerId) }.singleOrNull()
        farmer?.let {
            FarmerResponse(
                id = it[Farmers.id].toString(),
                userId = it[Farmers.userId].toString(),
                name = it[Farmers.name],
                aadhaarNumber = it[Farmers.aadhaarNumber],
                district = it[Farmers.district],
                taluk = it[Farmers.taluk],
                village = it[Farmers.village],
                address = it[Farmers.address],
                landRegistrationNumber = it[Farmers.landRegistrationNumber],
                verificationStatus = it[Farmers.verificationStatus],
                verifiedAt = it[Farmers.verifiedAt]?.toString(),
                rejectionReason = it[Farmers.rejectionReason]
            )
        }
    }
    
    suspend fun getFarmerByUserId(userId: String): FarmerResponse? = newSuspendedTransaction(Dispatchers.IO) {
        val farmer = Farmers.selectAll().where { Farmers.userId eq UUID.fromString(userId) }.singleOrNull()
        farmer?.let {
            FarmerResponse(
                id = it[Farmers.id].toString(),
                userId = it[Farmers.userId].toString(),
                name = it[Farmers.name],
                aadhaarNumber = it[Farmers.aadhaarNumber],
                district = it[Farmers.district],
                taluk = it[Farmers.taluk],
                village = it[Farmers.village],
                address = it[Farmers.address],
                landRegistrationNumber = it[Farmers.landRegistrationNumber],
                verificationStatus = it[Farmers.verificationStatus],
                verifiedAt = it[Farmers.verifiedAt]?.toString(),
                rejectionReason = it[Farmers.rejectionReason]
            )
        }
    }
    
    suspend fun createCropPlanting(
        farmerId: String,
        request: CropPlantingRequest,
        createdBy: String
    ): CropPlantingResponse = newSuspendedTransaction(Dispatchers.IO) {
        val plantingId = UUID.randomUUID()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        
        CropPlantings.insert {
            it[id] = plantingId
            it[CropPlantings.farmerId] = UUID.fromString(farmerId)
            it[CropPlantings.cropType] = request.cropType
            it[CropPlantings.season] = request.season
            it[CropPlantings.year] = request.year
            it[CropPlantings.areaMeasurement] = request.areaMeasurement.toBigDecimal()
            it[CropPlantings.measurementUnit] = request.measurementUnit
            it[CropPlantings.district] = request.district
            it[CropPlantings.taluk] = request.taluk
            it[CropPlantings.village] = request.village
            it[CropPlantings.plotDetails] = request.plotDetails
            it[CropPlantings.plantingDate] = request.plantingDate?.let { LocalDate.parse(it, formatter) }
            it[CropPlantings.expectedHarvestDate] = request.expectedHarvestDate?.let { LocalDate.parse(it, formatter) }
            it[CropPlantings.status] = "PLANTED"
            it[CropPlantings.createdBy] = UUID.fromString(createdBy)
            it[CropPlantings.updatedBy] = UUID.fromString(createdBy)
        }
        
        CropPlantingResponse(
            id = plantingId.toString(),
            farmerId = farmerId,
            cropType = request.cropType,
            season = request.season,
            year = request.year,
            areaMeasurement = request.areaMeasurement,
            measurementUnit = request.measurementUnit,
            district = request.district,
            taluk = request.taluk,
            village = request.village,
            plotDetails = request.plotDetails,
            plantingDate = request.plantingDate,
            expectedHarvestDate = request.expectedHarvestDate,
            status = "PLANTED"
        )
    }
    
    suspend fun getCropPlantings(farmerId: String): List<CropPlantingResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            CropPlantings.selectAll().where { CropPlantings.farmerId eq UUID.fromString(farmerId) }
                .map {
                    CropPlantingResponse(
                        id = it[CropPlantings.id].toString(),
                        farmerId = it[CropPlantings.farmerId].toString(),
                        cropType = it[CropPlantings.cropType],
                        season = it[CropPlantings.season],
                        year = it[CropPlantings.year],
                        areaMeasurement = it[CropPlantings.areaMeasurement].toDouble(),
                        measurementUnit = it[CropPlantings.measurementUnit],
                        district = it[CropPlantings.district],
                        taluk = it[CropPlantings.taluk],
                        village = it[CropPlantings.village],
                        plotDetails = it[CropPlantings.plotDetails],
                        plantingDate = it[CropPlantings.plantingDate]?.toString(),
                        expectedHarvestDate = it[CropPlantings.expectedHarvestDate]?.toString(),
                        status = it[CropPlantings.status]
                    )
                }
        }
    
    suspend fun uploadDocument(
        farmerId: String,
        documentType: String,
        documentName: String,
        filePath: String,
        fileSizeBytes: Long?,
        mimeType: String?,
        createdBy: String
    ): DocumentUploadResponse = newSuspendedTransaction(Dispatchers.IO) {
        val documentId = UUID.randomUUID()
        
        FarmerDocuments.insert {
            it[id] = documentId
            it[FarmerDocuments.farmerId] = UUID.fromString(farmerId)
            it[FarmerDocuments.documentType] = documentType
            it[FarmerDocuments.documentName] = documentName
            it[FarmerDocuments.filePath] = filePath
            it[FarmerDocuments.fileSizeBytes] = fileSizeBytes
            it[FarmerDocuments.mimeType] = mimeType
            it[FarmerDocuments.verificationStatus] = "PENDING"
            it[FarmerDocuments.createdBy] = UUID.fromString(createdBy)
            it[FarmerDocuments.updatedBy] = UUID.fromString(createdBy)
        }
        
        DocumentUploadResponse(
            documentId = documentId.toString(),
            filePath = filePath,
            documentType = documentType,
            verificationStatus = "PENDING"
        )
    }
}

