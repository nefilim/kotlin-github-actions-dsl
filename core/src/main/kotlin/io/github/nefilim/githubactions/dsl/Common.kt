package io.github.nefilim.githubactions.dsl

import io.github.nefilim.githubactions.domain.*

class EnvBuilder() {
    private val map = HashMap<String, String>()

    infix fun String.to (value: String) {
        map[this] = value
    }

    fun build(): Environment = map.toMap()
}

fun String.jobID(): WorkflowCommon.JobID = WorkflowCommon.JobID(this)
fun String.stepID(): WorkflowCommon.Job.Step.StepID = WorkflowCommon.Job.Step.StepID(this)
fun String.adhoc(): GitHubActionParameter = AdhocParameter(this)
fun String.adhocInput(): GitHubActionInputParameter = AdhocInputParameter(this)
fun String.adhocOutput(): GitHubActionOutputParameter = AdhocOutputParameter(this)