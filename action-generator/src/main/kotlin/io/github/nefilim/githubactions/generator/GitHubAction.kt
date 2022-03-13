package io.github.nefilim.githubactions.generator

@kotlinx.serialization.Serializable
data class GitHubActionStub(
    val name: String,
    val description: String,
    val inputs: Map<String, Input>,
    val outputs: Map<String, Output>? = null,
)

@kotlinx.serialization.Serializable
data class Input(
    val description: String = "",
    val default: String? = null,
    val required: Boolean? = null,
    val deprecationMessage: String? = null,
)

@kotlinx.serialization.Serializable
data class Output(
    val description: String,
)