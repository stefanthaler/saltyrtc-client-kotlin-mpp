rootProject.name = "saltyrtc-mpp"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }

    plugins {
        fun version(artifactId: String): String =
            extra["${artifactId}.version"] as String

        val kotlinVersion = version("kotlin")
        kotlin("multiplatform") version kotlinVersion
        kotlin("jvm") version kotlinVersion
        // TODO kotlin("js") version kotlinVersion
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
    }
}

// library
include(":api")
include(":core")
include(":crypto")

// websocket
include(":websocket-ktor")

// samples
include(":sample-jvm")
