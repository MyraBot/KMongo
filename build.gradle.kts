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
version = "0.11"

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
    maven(url = "https://m5rian.jfrog.io/artifactory/java")
    maven(url = "https://systems.myra.bot/releases") {
        credentials {
            username = System.getenv("REPO_NAME")
            password = System.getenv("REPO_SECRET")
        }
    }
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


    // Coroutines
    testImplementation("$kotlinxGroup:kotlinx-coroutines-core:$kotlinxCoroutines")
    testImplementation("$kotlinxGroup:kotlinx-coroutines-jdk8:$kotlinxCoroutines")
    // KMongo
    testImplementation(group = "org.litote.kmongo", name = "kmongo", version = kmongo)
    testImplementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = kotlinxSerialization)

    testImplementation(group = "com.github.myraBot", name = "Diskord", version = diskord) // Discord Wrapper
    testImplementation(group = "com.github.myraBot", name = "Slasher", version = slasher) // Command handler
    testImplementation(group = "com.github.m5rian", name = "Kotlingua", version = kotlingua)
}

/* publishing */
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    repositories {
        publications {
            create<MavenPublication>("repo") {
                group = project.group as String
                version = project.version as String
                artifactId = id
                from(components["java"])
            }
        }
        maven {
            url = uri( "https://systems.myra.bot/releases/")
            name = "repo"
            credentials {
                username = System.getenv("REPO_NAME")
                password = System.getenv("REPO_SECRET")
            }
            authentication { create<BasicAuthentication>("basic") }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "13"
}