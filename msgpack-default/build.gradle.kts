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
            }
        }

        val androidMain by getting {
            dependencies { // TODO android-specific library
                // JVM message packing
                implementation("org.msgpack:msgpack-core:0.8.+")
                implementation("org.msgpack:jackson-dataformat-msgpack:0.8.+")

                // Override some jackson-dataformat-msgpack 2.9 dependencies due to security vulnerabilities
                implementation("com.fasterxml.jackson.core:jackson-databind:2.10.+")
            }
        }

        val jvmMain by getting {
            dependencies {
                // JVM message packing
                implementation("org.msgpack:msgpack-core:0.8.+")
                implementation("org.msgpack:jackson-dataformat-msgpack:0.8.+")

                // Override some jackson-dataformat-msgpack 2.9 dependencies due to security vulnerabilities
                implementation("com.fasterxml.jackson.core:jackson-databind:2.10.+")
            }
        }
    }
}