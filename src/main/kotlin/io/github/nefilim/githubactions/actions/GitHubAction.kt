package io.github.nefilim.githubactions.actions

import io.github.nefilim.githubactions.domain.Environment
import io.github.nefilim.githubactions.domain.WorkflowCommon.Job.Step

interface GitHubAction {
    val name: String
    val description: String

    fun toStep(id: Step.StepID, name: String, uses: String, predicate: String?, env: Environment?): Step
}