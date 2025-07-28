plugins {
    kotlin("jvm") version "2.1.20"
}

group = "pl.cheily"
version = "1.1"

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