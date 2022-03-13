package io.github.nefilim.githubactions

import io.github.nefilim.githubactions.domain.AdhocParameter
import io.github.nefilim.githubactions.domain.GitHubActionInputParameter
import io.github.nefilim.githubactions.domain.GitHubActionOutputParameter
import io.github.nefilim.githubactions.domain.GitHubActionParameter
import io.github.nefilim.githubactions.domain.StepID
import io.github.nefilim.githubactions.domain.WorkflowCommon

fun param(name: String, value: String): Pair<GitHubActionParameter, String> = AdhocParameter(name) to value
fun param(parameter: GitHubActionParameter, value: String): Pair<GitHubActionParameter, String> = parameter to value

fun expression(exp: String): String = "\${{ $exp }}"

fun githubRef(name: String): String = expression("github.$name")
fun githubRepository(): String = githubRef("repository")

fun shellOutput(parameter: GitHubActionOutputParameter, value: String): String = "echo \"::set-output name=${parameter.parameter}::$value\""

fun outputRef(jobID: WorkflowCommon.JobID, name: String): String = expression("needs.${jobID.id}.outputs.$name")
fun outputRef(jobID: WorkflowCommon.JobID, parameter: GitHubActionOutputParameter): String = expression("needs.${jobID.id}.outputs.${parameter.parameter}")
fun outputRef(stepID: StepID, name: String): String = expression("steps.${stepID.id}.outputs.$name")
fun outputRef(stepID: StepID, parameter: GitHubActionOutputParameter): String = expression("steps.${stepID.id}.outputs.${parameter.parameter}")
fun nestedOutputRef(stepID: StepID, parameter: GitHubActionOutputParameter): String = "steps.${stepID.id}.outputs.${parameter.parameter}"

fun inputRef(name: String): String = expression("github.event.inputs.$name")
fun inputRef(parameter: GitHubActionInputParameter): String = expression("github.event.inputs.${parameter.parameter}")
fun nestedInputRef(parameter: GitHubActionInputParameter): String = "github.event.inputs.${parameter.parameter}"

fun actionInputRef(name: String): String = expression("inputs.$name")
fun actionInputRef(parameter: GitHubActionInputParameter): String = expression("inputs.${parameter.parameter}")
fun nestedActionInputRef(parameter: String): String = "inputs.${parameter}"
fun nestedActionInputRef(parameter: GitHubActionInputParameter): String = "inputs.${parameter.parameter}"

fun secretRef(name: String): String = expression("secrets.$name")
fun envRef(name: String): String = expression("env.$name")