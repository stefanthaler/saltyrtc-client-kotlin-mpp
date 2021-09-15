mapOf(
    "ktorVersion" to "1.6.0",
    "coroutinesVersion" to "1.5.2"
).forEach { (name, version) ->
    project.extra.set(name, version)
}