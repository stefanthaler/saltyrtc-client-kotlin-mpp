import org.gradle.api.Project

fun Project.v(artifactId: String): String =
    property("$artifactId.version") as String

fun Project.i(artifactId: String): Int =
    (property("$artifactId.version") as String).toInt()