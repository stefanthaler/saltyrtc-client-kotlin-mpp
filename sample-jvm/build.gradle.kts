plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))
    implementation(project(":crypto"))
    implementation(project(":websocket-ktor"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${v("coroutines")}")

    // TODO remove
    implementation("io.ktor:ktor-websockets:${v("ktor")}")
    implementation("io.ktor:ktor-client-websockets:${v("ktor")}")
    implementation("io.ktor:ktor-client-cio:${v("ktor")}")
}