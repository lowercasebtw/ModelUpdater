plugins {
    kotlin("jvm") version "2.0.20"
}

group = "btw.lowercase.modelupdater"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
}

kotlin {
    jvmToolchain(21)
}