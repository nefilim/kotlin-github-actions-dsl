package io.github.nefilim.githubactions.dsl

import io.github.nefilim.githubactions.domain.Environment

class EnvBuilder() {
    private val map = HashMap<String, String>()

    infix fun String.to (value: String) {
        map[this] = value
    }

    fun build(): Environment = map.toMap()
}
