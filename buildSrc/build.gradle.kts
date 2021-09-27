import java.util.*

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

val props = Properties().apply {
    file("../gradle.properties")
        .inputStream()
        .use { load(it) }
}

fun v(artifactId: String): String =
    props.getProperty("$artifactId.version")

dependencies {
    implementation(kotlin("gradle-plugin", v("kotlin")))
    implementation("com.android.tools.build:gradle:${v("android.agp")}")
}
