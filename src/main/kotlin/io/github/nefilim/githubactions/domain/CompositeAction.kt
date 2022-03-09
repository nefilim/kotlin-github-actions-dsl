@file:OptIn(ExperimentalSerializationApi::class)

package io.github.nefilim.githubactions.domain

import io.github.nefilim.githubactions.StepIDSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompositeAction(
    val name: String,
    val description: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val author: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val inputs: Map<GitHubActionInputParameter, Input>? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val outputs: Map<GitHubActionOutputParameter, Output>? = null,
    val runs: Runs,
) {
    @Serializable
    data class Input(
        val description: String,
        val required: Boolean,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val default: String? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val deprecationMessage: String? = null,
    )

    @Serializable
    data class Output(
        val value: String,
    )

    @Serializable
    data class Runs(
        val using: String,
        val steps: List<Step>,
    ) {
        @Serializable
        data class Step(
            @EncodeDefault(EncodeDefault.Mode.NEVER) val id: CStepID? = null,
            @EncodeDefault(EncodeDefault.Mode.NEVER) val name: String? = null,
            @EncodeDefault(EncodeDefault.Mode.NEVER) val shell: String? = null,
            @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("if") val predicate: String? = null,
            @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("working-directory") val workingDirectory: String? = null,
            @EncodeDefault(EncodeDefault.Mode.NEVER) val uses: String? = null,
            @EncodeDefault(EncodeDefault.Mode.NEVER) val run: String? = null,
            @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("with") val parameters: Map<GitHubActionParameter, String>? = null,
            @EncodeDefault(EncodeDefault.Mode.NEVER) val env: Environment? = null,
        ) {
            @Serializable(with = StepIDSerializer::class)
            data class CStepID(override val id: String): StepID
        }
    }
}