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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${v("coroutines")}")
            }
        }
    }
}