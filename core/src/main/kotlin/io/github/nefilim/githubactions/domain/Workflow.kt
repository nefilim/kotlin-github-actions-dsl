@file:OptIn(ExperimentalSerializationApi::class)

package io.github.nefilim.githubactions.domain

import io.github.nefilim.githubactions.InputSerializer
import io.github.nefilim.githubactions.JobIDSerializer
import io.github.nefilim.githubactions.StepIDSerializer
import io.github.nefilim.githubactions.StepSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Workflow(
    val name: String,
    val on: Triggers,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val concurrency: WorkflowCommon.Concurrency? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("env") val environment: Environment? = null,
    val jobs: LinkedHashMap<WorkflowCommon.JobID, WorkflowCommon.Job> = LinkedHashMap(),
) {
    @Serializable
    data class Triggers(
        @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("workflow_dispatch") val workflowDispatch: Trigger.WorkflowDispatch? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val push: Trigger.Push? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("pull_request") val pullRequest: Trigger.PullRequest? = null,
    ) {
        @Serializable
        sealed class Trigger {
            @Serializable
            @SerialName("push")
            data class Push(
                @EncodeDefault(EncodeDefault.Mode.NEVER) val branches: List<String> = emptyList(), // verify globs?
                @EncodeDefault(EncodeDefault.Mode.NEVER) val tags: List<String> = emptyList(),
                @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("branches-ignore") val branchesIgnore: List<String> = emptyList(),
                @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("tags-ignore") val tagsIgnore: List<String> = emptyList(),
                @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("paths-ignore") val pathsIgnore: List<String> = emptyList(),
            ): Trigger()

            @Serializable
            @SerialName("pull_request")
            data class PullRequest(
                @EncodeDefault(EncodeDefault.Mode.NEVER) val branches: List<String> = emptyList(), // verify globs?
                @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("branches-ignore") val branchesIgnore: List<String> = emptyList(),
                @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("paths-ignore") val pathsIgnore: List<String> = emptyList(),
            ): Trigger()

            @Serializable
            @SerialName("workflow_dispatch")
            data class WorkflowDispatch(
                val inputs: Map<GitHubActionInputParameter, WorkflowCommon.Input> = emptyMap()
            ): Trigger()
        }
    }
}

@Serializable
data class ReusableWorkflow(
    val name: String,
    val on: ReusableWorkflowTrigger,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val concurrency: WorkflowCommon.Concurrency? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("env") val environment: Environment? = null,
    val jobs: LinkedHashMap<WorkflowCommon.JobID, WorkflowCommon.Job> = LinkedHashMap(),
) {

    @Serializable
    data class ReusableWorkflowTrigger(
        @SerialName("workflow_call") val workflowCall: WorkflowCall,
    ) {
        @Serializable
        data class WorkflowCall(
            val inputs: Map<GitHubActionInputParameter, WorkflowCommon.Input> = emptyMap(),
            val outputs: Map<GitHubActionOutputParameter, WorkflowCommon.Output> = emptyMap(),
        )
    }
}

object WorkflowCommon {
    @Serializable
    data class Concurrency(
        val group: String,
        @SerialName("cancel-in-progress") val cancelInProgress: Boolean = false,
    )

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

    @Serializable
    data class Output(
        val description: String,
        val value: String,
    )

    @Serializable(with = JobIDSerializer::class)
    data class JobID(
        val id: String
    )

    @Serializable
    data class Job(
        @EncodeDefault(EncodeDefault.Mode.NEVER) val needs: JobID? = null,
        @SerialName("runs-on") val runsOn: List<String>,
        @SerialName("if") val predicate: String? = null,
        val steps: List<Step>,
        val outputs: Map<GitHubActionOutputParameter, String> = emptyMap(),
    ) {
        @Serializable(with = StepSerializer::class)
        sealed class Step {
            abstract val name: String
            abstract val id: StepID?
            abstract val outputs: Map<GitHubActionOutputParameter, String>

            @Serializable(with = StepIDSerializer::class)
            data class StepID(override val id: String): io.github.nefilim.githubactions.domain.StepID // swap to value class once gradle gets its shit together and move to 1.6

            @Serializable
            data class Uses(
                override val name: String,
                val uses: String,
                @SerialName("with") val parameters: Map<GitHubActionParameter, String>,
                @EncodeDefault(EncodeDefault.Mode.NEVER) override val id: StepID? = null,
                @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("if") val predicate: String? = null,
                @EncodeDefault(EncodeDefault.Mode.NEVER) val env: Environment? = null,
                @Transient override val outputs: Map<GitHubActionOutputParameter, String> = emptyMap(),
            ): Step() {
                constructor(name: String, uses: String, id: StepID, parameters: Map<GitHubActionParameter, String>, predicate: String? = null, env: Environment? = null, outputs: Map<GitHubActionOutputParameter, String> = emptyMap()): this(name, uses, parameters, id, predicate, env, outputs)
            }

            @Serializable
            data class Runs(
                override val name: String,
                val run: String,
                @EncodeDefault(EncodeDefault.Mode.NEVER) override val id: StepID? = null,
                @EncodeDefault(EncodeDefault.Mode.NEVER) val shell: String? = null,
                @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("if") val predicate: String? = null,
                @EncodeDefault(EncodeDefault.Mode.NEVER) val env: Environment? = null,
                @Transient override val outputs: Map<GitHubActionOutputParameter, String> = emptyMap(),
            ): Step() {
                constructor(name: String, run: List<String>): this(name, run.joinToString("\n"))

                companion object {
                    fun python(name: String, run: List<String>, id: StepID? = null) = Runs(name, run.joinToString("\n"), id, "python")
                    fun python(name: String, run: String) = Runs(name, run, null, "python")
                    fun python(name: String, id: StepID, run: String, predicate: String? = null, env: Environment? = null, outputs: Map<GitHubActionOutputParameter, String> = emptyMap()) = Runs(name, run, id, "python", predicate, env, outputs)
                }
            }
        }
    }
}