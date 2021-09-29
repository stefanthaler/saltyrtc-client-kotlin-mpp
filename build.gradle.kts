//  root gradle build file
subprojects {
    apply(plugin = "kotlin-library-conventions")
}

allprojects {
    val kotlinVersion = property("kotlin.version") as String

    configurations.all {
        resolutionStrategy {
            eachDependency {
                if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-"))
                    useVersion(kotlinVersion)
            }
        }
    }
}