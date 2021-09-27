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
                implementation(project(":core"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${v("coroutines")}")
            }
        }
    }
}