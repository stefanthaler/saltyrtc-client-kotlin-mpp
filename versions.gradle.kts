mapOf(
        "ktor_version" to "1.4.0",
        "coroutines_version" to "1.3.9"
).forEach { (name, version) ->
    project.extra.set(name, version)
}