import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

tasks.withType<KotlinCompile<*>>().configureEach {
    kotlinOptions {
        allWarningsAsErrors = false
        languageVersion = "1.5"
        apiVersion = "1.5"
    }
}