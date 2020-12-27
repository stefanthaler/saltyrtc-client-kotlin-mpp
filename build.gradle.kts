// https://github.com/gradle/kotlin-dsl-samples/tree/master/samples
//
apply(from = "versions.gradle.kts")
val ktor_version: String by extra
val coroutines_version: String by extra

plugins {
    kotlin("multiplatform") version "1.4.0"
}
group = "net.thaler-it.saltyrtc.kotlin-mpp"
version = "0.0.1-SNAPSHOT"




repositories {
    mavenCentral()
    jcenter()
}
kotlin {


    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
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
                // KTOR Web Sockets dependencies
                implementation("io.ktor:ktor-websockets:$ktor_version")
                implementation("io.ktor:ktor-client-websockets:$ktor_version")
                implementation("io.ktor:ktor-client-cio:$ktor_version")
               // implementation("io.ktor:ktor-client-js:$ktor_version")
                //implementation("io.ktor:ktor-client-okhttp:$ktor_version")

                // coroutines version
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
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
                // JVM message packing
                implementation("org.msgpack:msgpack-core:0.8.+")
                implementation("org.msgpack:jackson-dataformat-msgpack:0.8.+")

                // Override some jackson-dataformat-msgpack 2.9 dependencies due to security vulnerabilities
                implementation ("com.fasterxml.jackson.core:jackson-databind:2.10.+")

                // NaCl library
                implementation ("co.libly:resource-loader:1.3.7")
                implementation ("net.java.dev.jna:jna:5.5.0")
                implementation ("com.goterl.lazycode:lazysodium-java:4.2.5")

                implementation( "io.github.microutils:kotlin-logging:1.8.3")
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