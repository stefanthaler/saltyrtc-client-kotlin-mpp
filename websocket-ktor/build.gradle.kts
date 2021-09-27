plugins {
    kotlin("multiplatform")

    `java-library-conventions`
    `android-library-conventions`
}

kotlin {
    jvm()

    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":api"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${v("coroutines")}")
                implementation("io.ktor:ktor-websockets:${v("ktor")}")
                implementation("io.ktor:ktor-client-websockets:${v("ktor")}")
                implementation("io.ktor:ktor-client-cio:${v("ktor")}")
            }
        }
    }
}