import org.gradle.api.Project

fun Project.v(artifactId: String): String =
    property("$artifactId.version") as String