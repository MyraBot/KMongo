import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.30"
    `maven-publish`
}

group = "com.github.myraBot"
val id = "KMongo"
version = "0.5"

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
    maven(url = "https://m5rian.jfrog.io/artifactory/java")
}
dependencies {
    val kotlinxGroup = "org.jetbrains.kotlinx"
    val kotlinxVersion = "1.5.2"

    val marianGroup = "com.github.m5rian"

    // Coroutines
    compileOnly("$kotlinxGroup:kotlinx-coroutines-core:$kotlinxVersion")
    compileOnly("$kotlinxGroup:kotlinx-coroutines-jdk8:$kotlinxVersion")
    // KMongo
    compileOnly(group = "org.litote.kmongo", name = "kmongo", version = "4.3.0")
    compileOnly(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.2.2")

    compileOnly(group = "com.github.myraBot", name = "Diskord", version = "0.63") // Discord Wrapper
    compileOnly(group = "com.github.myraBot", name = "Slasher", version = "0.1") // Command handler
    compileOnly(group = marianGroup, name = "Kotlingua", version = "0.5")


    // Coroutines
    testImplementation("$kotlinxGroup:kotlinx-coroutines-core:$kotlinxVersion")
    testImplementation("$kotlinxGroup:kotlinx-coroutines-jdk8:$kotlinxVersion")
    // KMongo
    testImplementation(group = "org.litote.kmongo", name = "kmongo", version = "4.3.0")
    testImplementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.2.2")
    // JDA
    testImplementation(group = "net.dv8tion", name = "JDA", version = "4.3.0_324")
    // Myra libraries
    testImplementation(group = marianGroup, name = "Kommand-handler", version = "development") // Command handler
    testImplementation(group = marianGroup, name = "Kotlingua", version = "0.5")
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