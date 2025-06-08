plugins {
    id("java-library")
}

dependencies {
    implementation(project(":core"))
    implementation("com.github.revanced:revanced-library:2.4.0")
}

tasks.register("generatePatchBundle", Jar::class) {
    archiveFileName.set("patches-${project.version}.rvpk")
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
}