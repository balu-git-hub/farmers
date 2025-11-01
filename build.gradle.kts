plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com.kug"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {

    // IMPORTANT: Exposed versions MUST be consistent.
    val ktorVersion = "3.0.0"
    val exposedVersion = "0.56.0" // Updated to 0.56.0 for consistency across all modules
    val postgresVersion = "42.7.2"

    // Ktor core
    implementation("io.ktor:ktor-server-core:${ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${ktorVersion}")
    implementation("io.ktor:ktor-server-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
    implementation("io.ktor:ktor-server-call-logging:${ktorVersion}")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")

    // Exposed ORM (All modules now use the same consistent version: 0.56.0)
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion") // This was already 0.56.0

    // PostgreSQL
    implementation("org.postgresql:postgresql:$postgresVersion")

    // Date/time
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    // Connection Pool
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Password hashing
    implementation("org.mindrot:jbcrypt:0.4")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation("ch.qos.logback:logback-classic:1.5.6")

}