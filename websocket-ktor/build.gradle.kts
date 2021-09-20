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
                implementation("io.ktor:ktor-websockets:${v("ktor")}")
                implementation("io.ktor:ktor-client-websockets:${v("ktor")}")
                implementation("io.ktor:ktor-client-cio:${v("ktor")}")
            }
        }
    }
}