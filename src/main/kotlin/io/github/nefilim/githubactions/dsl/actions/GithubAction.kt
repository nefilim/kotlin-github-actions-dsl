package io.github.nefilim.githubactions.dsl.actions

import io.github.nefilim.githubactions.dsl.Step

interface GithubAction {
    val name: String
    val description: String

    fun toStep(id: Step.StepID, name: String, uses: String): Step
}