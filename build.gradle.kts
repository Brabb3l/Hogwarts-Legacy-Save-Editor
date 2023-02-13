plugins {
    application
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
}

group = "de.brabb3l"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("org.xerial:sqlite-jdbc:3.40.1.0")
}

application {
    mainClass.set("de.brabb3l.hlse.MainKt")
}