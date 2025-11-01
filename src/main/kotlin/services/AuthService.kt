package com.payir.services

import com.payir.dto.*
import com.payir.models.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class AuthService {
    
    suspend fun register(request: RegisterRequest): RegisterResponse = newSuspendedTransaction(Dispatchers.IO) {
        // Check if username already exists
        val existingUser = Users.selectAll().where { Users.username eq request.username }.singleOrNull()
        if (existingUser != null) {
            throw IllegalArgumentException("Username already exists")
        }
        
        val userId = UUID.randomUUID()
        val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
        
        Users.insert { insert ->
            insert[Users.id] = userId
            insert[Users.username] = request.username
            insert[Users.passwordHash] = hashedPassword
            insert[Users.role] = request.role
            insert[Users.email] = request.email
            insert[Users.phone] = request.phone
            insert[Users.isActive] = true
            insert[Users.createdBy] = userId
            insert[Users.updatedBy] = userId
        }
        
        RegisterResponse(
            userId = userId.toString(),
            username = request.username,
            role = request.role
        )
    }
    
    suspend fun login(request: LoginRequest): AuthResponse = newSuspendedTransaction(Dispatchers.IO) {
        val user = Users.selectAll().where { Users.username eq request.username }
            .singleOrNull()
            ?: throw IllegalArgumentException("Invalid username or password")
        
        if (!user[Users.isActive]) {
            throw IllegalArgumentException("Account is inactive")
        }
        
        val passwordHash = user[Users.passwordHash]
        if (!BCrypt.checkpw(request.password, passwordHash)) {
            throw IllegalArgumentException("Invalid username or password")
        }
        
        // For MVP, we're using Basic Auth, so token is optional
        // In production, you'd generate a session token here
        val token = generateBasicAuthToken(user[Users.username], request.password)
        
        AuthResponse(
            userId = user[Users.id].toString(),
            username = user[Users.username],
            role = user[Users.role],
            token = token
        )
    }
    
    suspend fun getUserById(userId: String): com.payir.dto.UserResponse? = newSuspendedTransaction(Dispatchers.IO) {
        val user = Users.selectAll().where { Users.id eq UUID.fromString(userId) }.singleOrNull()
        user?.let {
            com.payir.dto.UserResponse(
                id = it[Users.id].toString(),
                username = it[Users.username],
                role = it[Users.role],
                email = it[Users.email],
                phone = it[Users.phone],
                isActive = it[Users.isActive]
            )
        }
    }
    
    private fun generateBasicAuthToken(username: String, password: String): String {
        val credentials = "$username:$password"
        return Base64.getEncoder().encodeToString(credentials.toByteArray())
    }
}

