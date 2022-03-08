package io.github.nefilim.githubactions.actions

import io.github.nefilim.githubactions.GitHubActionParameterSerializer
import io.github.nefilim.githubactions.domain.Workflow.Job.Step

interface GitHubAction {
    val name: String
    val description: String

    fun toStep(id: Step.WStepID, name: String, uses: String): Step
}

@kotlinx.serialization.Serializable(with = GitHubActionParameterSerializer::class)
interface GitHubActionParameter {
    val parameter: String
}

@kotlinx.serialization.Serializable(with = GitHubActionParameterSerializer::class)
interface GitHubActionInputParameter: GitHubActionParameter
@kotlinx.serialization.Serializable(with = GitHubActionParameterSerializer::class)
interface GitHubActionOutputParameter: GitHubActionParameter

class AdhocParameter(override val parameter: String): GitHubActionParameter
class AdhocInputParameter(override val parameter: String): GitHubActionInputParameter
class AdhocOutputParameter(override val parameter: String): GitHubActionOutputParameter

fun Map<String, String>.adhoc(): Map<GitHubActionParameter, String> = this.mapKeys { AdhocParameter(it.key) }
fun Map<String, String>.adhocInput(): Map<GitHubActionInputParameter, String> = this.mapKeys { AdhocInputParameter(it.key) }
fun Map<String, String>.adhocOutput(): Map<GitHubActionOutputParameter, String> = this.mapKeys { AdhocOutputParameter(it.key) }