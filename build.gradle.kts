import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("io.mongock:mongock-bom:5.2.2")
    }
}

dependencies {
    implementation("io.mongock:mongock-springboot-v3")
    implementation("io.mongock:mongodb-springdata-v4-driver")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")

    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("com.ninja-squad:springmockk:4.0.0")
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:mongodb:1.17.6")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
