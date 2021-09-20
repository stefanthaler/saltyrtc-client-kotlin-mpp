// https://github.com/gradle/kotlin-dsl-samples/tree/master/samples
plugins {
    kotlin("multiplatform")

    `java-library-conventions`
}

kotlin {
    jvm()
//    js {
//        browser {
//            testTask {
//                useKarma {
//                    useChromeHeadless()
//                    webpackConfig.cssSupport.enabled = true
//                }
//            }
//        }
//    }
//    val hostOs = System.getProperty("os.name")
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" -> macosX64("native")
//        hostOs == "Linux" -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }

//    nativeTarget.apply {
//        binaries {
//            executable {
//                entryPoint = "main"
//            }
//        }
//    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":api"))
                implementation(project(":crypto"))
                implementation(kotlin("stdlib"))
                implementation("io.ktor:ktor-utils:${v("ktor")}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${v("coroutines")}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
                // JVM message packing
                implementation("org.msgpack:msgpack-core:0.8.+")
                implementation("org.msgpack:jackson-dataformat-msgpack:0.8.+")

                // Override some jackson-dataformat-msgpack 2.9 dependencies due to security vulnerabilities
                implementation("com.fasterxml.jackson.core:jackson-databind:2.10.+")

                implementation("io.github.microutils:kotlin-logging:1.8.3")
                implementation("org.slf4j:slf4j-simple:1.7.29")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
//        val jsMain by getting
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }
//        val nativeMain by getting
//        val nativeTest by getting
    }
}