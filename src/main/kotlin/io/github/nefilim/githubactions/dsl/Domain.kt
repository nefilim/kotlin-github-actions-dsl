@file:OptIn(ExperimentalSerializationApi::class)

package io.github.nefilim.githubactions.dsl

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Workflow(
    val name: String,
    val on: Triggers,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val concurrency: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("env") val environment: Environment? = null,
    val jobs: Map<String, Job> = emptyMap(),
)

@Serializable
data class Triggers(
    @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("workflow_dispatch") val workflowDispatch: Trigger.WorkflowDispatch? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val push: Trigger.Push? = null,
)

@Serializable
sealed class Trigger {
    @Serializable
    @SerialName("push")
    data class Push(
        @EncodeDefault(EncodeDefault.Mode.NEVER) val branches: List<String> = emptyList(), // verify globs?
        @EncodeDefault(EncodeDefault.Mode.NEVER) val tags: List<String> = emptyList(),
        @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("branches-ignore") val branchesIgnore: List<String> = emptyList(),
        @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("tags-ignore") val tagsIgnore: List<String> = emptyList(),
    ): Trigger()

    @Serializable
    @SerialName("workflow_dispatch")
    data class WorkflowDispatch(
        val inputs: Map<String, Input> = emptyMap()
    ): Trigger() {
        companion object {
            @Serializable
            enum class Type {
                @SerialName("boolean") Boolean,
                @SerialName("choice") Choice,
                @SerialName("environment") Environment,
                @SerialName("string") String;

                override fun toString(): kotlin.String {
                    return this.name.lowercase()
                }
            }

            @Serializable(with = InputSerializer::class)
            sealed class Input {
                abstract val description: kotlin.String
                abstract val required: kotlin.Boolean
                abstract val type: Type

                @Serializable
                data class Boolean(
                    override val description: kotlin.String,
                    @EncodeDefault(EncodeDefault.Mode.NEVER) val default: kotlin.Boolean? = null,
                    override val required: kotlin.Boolean = false,
                ): Input() {
                    override val type: Type = Type.Boolean
                }

                @Serializable
                data class Choice(
                    override val description: kotlin.String,
                    val options: List<kotlin.String>,
                    @EncodeDefault(EncodeDefault.Mode.NEVER) val default: kotlin.String? = null,
                    override val required: kotlin.Boolean = false,
                ): Input() {
                    override val type: Type = Type.Choice
                }

                @Serializable
                data class Environment(
                    override val description: kotlin.String,
                    override val required: kotlin.Boolean = false,
                ): Input() {
                    override val type: Type = Type.Environment
                }

                @Serializable
                data class String(
                    override val description: kotlin.String,
                    @EncodeDefault(EncodeDefault.Mode.NEVER) val default: kotlin.String? = null,
                    override val required: kotlin.Boolean = false,
                ): Input() {
                    override val type: Type = Type.String
                }
            }
        }
    }
}

@Serializable
data class Job(
    @SerialName("runs-on") val runsOn: List<String>,
    val steps: List<Step>,
)

@Serializable(with = StepSerializer::class)
sealed class Step {
    abstract val name: String
    abstract val id: StepID?

    @Serializable(with = StepIDSerializer::class)
    data class StepID(val id: String) // swap to value class once gradle gets its shit together and move to 1.6

    @Serializable
    data class Uses(
        override val name: String,
        val uses: String,
        @SerialName("with") val parameters: Map<String, String>,
        @EncodeDefault(EncodeDefault.Mode.NEVER) override val id: StepID? = null,
    ): Step() {
        constructor(name: String, uses: String, id: StepID, parameters: Map<String, String>): this(name, uses, parameters, id)
    }

    @Serializable
    data class Runs(
        override val name: String,
        val run: String,
        @EncodeDefault(EncodeDefault.Mode.NEVER) override val id: StepID? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val shell: String? = null,
    ): Step() {
        constructor(name: String, run: List<String>, shell: String? = null, id: StepID? = null): this(name, run.joinToString("\n"), id, shell)

        companion object {
            fun python(name: String, run: List<String>, id: StepID? = null) = Runs(name, run.joinToString("\n"), id, "python")
            fun python(name: String, run: String) = Runs(name, run, null, "python")
            fun python(name: String, id: StepID, run: String) = Runs(name, run, id, "python")
        }
    }

}

typealias Environment = Map<String, String>