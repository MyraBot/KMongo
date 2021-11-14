import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val kotlinxCoroutines: String by project
val kotlinxSerialization: String by project
val kmongo: String by project
val diskord: String by project
val slasher: String by project
val kotlingua: String by project

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.30"
    `maven-publish`
}

group = "com.github.myraBot"
val id = "KMongo"
version = "0.6"

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
    maven(url = "https://m5rian.jfrog.io/artifactory/java")
}
dependencies {
    val kotlinxGroup = "org.jetbrains.kotlinx"

    // Coroutines
    compileOnly("$kotlinxGroup:kotlinx-coroutines-core:$kotlinxCoroutines")
    compileOnly("$kotlinxGroup:kotlinx-coroutines-jdk8:$kotlinxCoroutines")
    // KMongo
    compileOnly(group = "org.litote.kmongo", name = "kmongo", version = kmongo)
    compileOnly(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = kotlinxSerialization)

    compileOnly(group = "com.github.myraBot", name = "Diskord", version = diskord) // Discord Wrapper
    compileOnly(group = "com.github.myraBot", name = "Slasher", version = slasher) // Command handler
    compileOnly(group = "com.github.m5rian", name = "Kotlingua", version = kotlingua)
}

/* publishing */
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    repositories {
        maven {
            name = "jfrog"
            url = uri("https://m5rian.jfrog.io/artifactory/java")
            credentials {
                username = System.getenv("JFROG_USERNAME")
                password = System.getenv("JFROG_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("jfrog") {
            from(components["java"])

            group = project.group as String
            version = project.version as String
            artifactId = id

            artifact(sourcesJar)
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "13"
}