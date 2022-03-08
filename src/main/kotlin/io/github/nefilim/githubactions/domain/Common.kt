package io.github.nefilim.githubactions.domain

import io.github.nefilim.githubactions.StepIDSerializer

typealias Environment = Map<String, String>

@kotlinx.serialization.Serializable(with = StepIDSerializer::class)
interface StepID {
    val id: String
}