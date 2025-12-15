plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    application
}

group = "com.polman.oop.diagram2code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // Ktor Server
    implementation("io.ktor:ktor-server-core-jvm:3.3.3")
    implementation("io.ktor:ktor-server-netty-jvm:3.3.3")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.3.3")
    implementation("io.ktor:ktor-server-status-pages-jvm:3.3.3")
    implementation("io.ktor:ktor-server-cors-jvm:3.3.3")

    // Ktor HTTP Client (UNTUK MIDTRANS)
    implementation("io.ktor:ktor-client-core-jvm:3.3.3")
    implementation("io.ktor:ktor-client-cio-jvm:3.3.3")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:3.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.3.3")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.52.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.52.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.52.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.52.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.mysql:mysql-connector-j:8.4.0")

    // Logging (AMAN)
    implementation("ch.qos.logback:logback-classic:1.5.13")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.3.3")
}

application {
    mainClass.set("app.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
