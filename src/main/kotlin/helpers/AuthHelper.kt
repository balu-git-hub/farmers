package com.payir.helpers

import com.payir.models.Users
import io.ktor.server.application.*
import io.ktor.server.auth.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.mindrot.jbcrypt.BCrypt

object AuthHelper {
    
    suspend fun validate(credentials: UserPasswordCredential): Principal? = 
        newSuspendedTransaction(Dispatchers.IO) {
            val user = Users.selectAll().where { 
                (Users.username eq credentials.name) and 
                (Users.isActive eq true)
            }.singleOrNull()
                ?: return@newSuspendedTransaction null
            
            val passwordHash = user[Users.passwordHash]
            if (!BCrypt.checkpw(credentials.password, passwordHash)) {
                return@newSuspendedTransaction null
            }
            
            UserPrincipal(
                userId = user[Users.id].toString(),
                username = user[Users.username],
                role = user[Users.role]
            )
        }
}

data class UserPrincipal(
    val userId: String,
    val username: String,
    val role: String
) : Principal

// Extension function to get authenticated user
fun ApplicationCall.getCurrentUser(): UserPrincipal? {
    return principal<UserPrincipal>()
}

// Extension function to require authentication
suspend fun ApplicationCall.requireAuth(): UserPrincipal {
    return principal<UserPrincipal>() 
        ?: throw io.ktor.server.plugins.BadRequestException("Authentication required")
}
