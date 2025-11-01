package com.payir.services

import com.payir.dto.*
import com.payir.models.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class LocationService {
    
    suspend fun getAllDistricts(): List<DistrictResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            Districts.selectAll()
                .map {
                    DistrictResponse(
                        id = it[Districts.id].toString(),
                        name = it[Districts.name],
                        state = it[Districts.state],
                        code = it[Districts.code]
                    )
                }
        }
    
    suspend fun getTaluksByDistrict(districtId: String): List<TalukResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            Taluks.selectAll().where { Taluks.districtId eq java.util.UUID.fromString(districtId) }
                .map {
                    TalukResponse(
                        id = it[Taluks.id].toString(),
                        districtId = it[Taluks.districtId].toString(),
                        name = it[Taluks.name],
                        code = it[Taluks.code]
                    )
                }
        }
    
    suspend fun getVillagesByTaluk(talukId: String): List<VillageResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            Villages.selectAll().where { Villages.talukId eq java.util.UUID.fromString(talukId) }
                .map {
                    VillageResponse(
                        id = it[Villages.id].toString(),
                        talukId = it[Villages.talukId].toString(),
                        name = it[Villages.name],
                        code = it[Villages.code]
                    )
                }
        }
    
    suspend fun getTaluksByDistrictName(districtName: String): List<TalukResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            val district = Districts.selectAll().where { Districts.name eq districtName }
                .singleOrNull()
                ?: return@newSuspendedTransaction emptyList()
            
            Taluks.selectAll().where { Taluks.districtId eq district[Districts.id] }
                .map {
                    TalukResponse(
                        id = it[Taluks.id].toString(),
                        districtId = it[Taluks.districtId].toString(),
                        name = it[Taluks.name],
                        code = it[Taluks.code]
                    )
                }
        }
    
    suspend fun getVillagesByTalukName(talukName: String): List<VillageResponse> = 
        newSuspendedTransaction(Dispatchers.IO) {
            val taluk = Taluks.selectAll().where { Taluks.name eq talukName }
                .singleOrNull()
                ?: return@newSuspendedTransaction emptyList()
            
            Villages.selectAll().where { Villages.talukId eq taluk[Taluks.id] }
                .map {
                    VillageResponse(
                        id = it[Villages.id].toString(),
                        talukId = it[Villages.talukId].toString(),
                        name = it[Villages.name],
                        code = it[Villages.code]
                    )
                }
        }
}

