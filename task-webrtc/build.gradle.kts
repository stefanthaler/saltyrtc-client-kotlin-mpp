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
                implementation(project(":core"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${v("coroutines")}")
            }
        }
    }
}