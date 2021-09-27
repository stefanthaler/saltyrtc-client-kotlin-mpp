plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))
    implementation(project(":crypto"))
    implementation(project(":msgpack-default"))
    implementation(project(":logger-default"))
    implementation(project(":websocket-ktor"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${v("coroutines")}")

    implementation(project(":task-relayed-data"))


    // TODO remove
    implementation("io.ktor:ktor-websockets:${v("ktor")}")
    implementation("io.ktor:ktor-client-websockets:${v("ktor")}")
    implementation("io.ktor:ktor-client-cio:${v("ktor")}")
}