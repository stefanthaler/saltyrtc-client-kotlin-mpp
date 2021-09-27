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
        kotlin("android") version kotlinVersion
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.library",
                "com.android.application",
                -> useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
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

// tasks
include(":task-relayed-data")
include(":task-webrtc")

// samples
include(":sample-jvm")
