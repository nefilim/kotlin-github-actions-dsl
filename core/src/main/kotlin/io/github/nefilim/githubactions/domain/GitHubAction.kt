package io.github.nefilim.githubactions.domain

import io.github.nefilim.githubactions.domain.WorkflowCommon.Job.Step

interface GitHubAction {
    val name: String
    val description: String

    fun toStep(id: Step.StepID? = null, name: String, predicate: String? = null, env: Environment? = null): Step
    fun toStep(): Step
}