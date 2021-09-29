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


            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.goterl:resource-loader:${v("jvm.resource-loader")}")
                implementation("com.goterl:lazysodium-java:${v("jvm.lazysodium")}")
            }
        }

        val androidMain by getting {
            dependencies {
                //implementation("net.java.dev.jna:jna:5.8.0@aar") // TODO ?
                implementation("com.goterl:resource-loader:${v("android.resource-loader")}")
                implementation("com.goterl:lazysodium-android:${v("android.lazysodium")}")
            }
        }
    }
}