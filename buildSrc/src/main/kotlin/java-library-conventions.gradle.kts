import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

tasks.withType<KotlinJvmCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}