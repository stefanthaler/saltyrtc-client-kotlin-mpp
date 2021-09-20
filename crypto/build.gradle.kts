plugins {
    kotlin("multiplatform")

    `java-library-conventions`
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":api"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${v("coroutines")}")

                implementation("com.goterl:resource-loader:${v("resource-loader")}")
                implementation("net.java.dev.jna:jna:5.5.0")
                implementation("com.goterl:lazysodium-java:${v("lazysodium-java")}")
            }
        }
    }
}