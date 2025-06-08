pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/CUPUL-MIU-04/revanced-patches-kitsune")
            credentials {
                // Versión corregida usando .get() para convertir Provider<String> a String
                username = providers.gradleProperty("gpr.user").getOrElse("") ?: System.getenv("GITHUB_ACTOR") ?: ""
                password = providers.gradleProperty("gpr.key").getOrElse("") ?: System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}