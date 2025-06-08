plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("maven-publish")
}

group = "app.kitsune"
version = "4.5.4"

repositories {
    mavenCentral()
    google()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.revanced:revanced-patcher:19.0.0")
    implementation("org.smali:dexlib2:2.5.2")
    testImplementation("junit:junit:4.13.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("Kitsune Patches")
                description.set("Custom ReVanced patches for Kitsune")
            }
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/CUPUL-MIU-04/revanced-patches-kitsune")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}