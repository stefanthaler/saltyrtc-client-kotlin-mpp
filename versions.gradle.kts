mapOf(
        "ktor_version" to "1.6.0",
        "coroutines_version" to "1.5.2"
).forEach { (name, version) ->
    project.extra.set(name, version)
}