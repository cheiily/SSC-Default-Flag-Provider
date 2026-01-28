plugins {
    kotlin("jvm") version "2.1.20"
    id("com.gradleup.shadow") version "9.2.2"
}

group = "pl.cheily"
version = "1.5.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20250517")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.shadowJar {
    archiveClassifier = ""
}
