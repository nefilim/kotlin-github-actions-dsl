@file:OptIn(ExperimentalSerializationApi::class)

package io.github.nefilim.githubactions.dsl

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
import io.github.nefilim.githubactions.GithubActionsWorkflowDSL
import io.github.nefilim.githubactions.domain.GitHubActionInputParameter
import io.github.nefilim.githubactions.domain.GitHubActionOutputParameter
import io.github.nefilim.githubactions.domain.ReusableWorkflow
import io.github.nefilim.githubactions.domain.Workflow
import io.github.nefilim.githubactions.domain.Workflow.Triggers
import io.github.nefilim.githubactions.domain.Workflow.Triggers.Trigger
import io.github.nefilim.githubactions.domain.WorkflowCommon
import io.github.nefilim.githubactions.domain.WorkflowCommon.Input
import io.github.nefilim.githubactions.domain.WorkflowCommon.Job
import io.github.nefilim.githubactions.domain.WorkflowCommon.Job.Step
import io.github.nefilim.githubactions.outputRef
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*

@GithubActionsWorkflowDSL
class WorkflowBuilder(
    private val name: String,
) {
    private var concurrency: WorkflowCommon.Concurrency? = null
    private val tb = TriggerBuilder()
    private val jb = JobsBuilder()
    private val eb = EnvBuilder()

    fun triggers(fn: TriggerBuilder.() -> Unit) {
        tb.fn()
    }

    fun concurrency(group: String, cancelInProgress: Boolean = false) {
        this.concurrency = WorkflowCommon.Concurrency(group, cancelInProgress)
    }

    fun jobs(fn: JobsBuilder.() -> Unit) {
        jb.fn()
    }

    fun env(fn: EnvBuilder.() -> Unit) {
        eb.fn()
    }

    internal fun build(): Workflow {
        // TODO validate triggers, can't have duplicates, validate jobs, cant have dups
        return Workflow(name, tb.build(), concurrency, eb.build().ifEmpty { null }, jb.build())
    }
}

class ReusableWorkflowBuilder(
    private val name: String,
) {
    private var concurrency: WorkflowCommon.Concurrency? = null

    private val jb = JobsBuilder()
    private val eb = EnvBuilder()

    private val wfcb: WorkflowCallBuilder = WorkflowCallBuilder()

    fun workflowCall(fn: WorkflowCallBuilder.() -> Unit) {
        wfcb.fn()
    }

    fun concurrency(group: String, cancelInProgress: Boolean = false) {
        this.concurrency = WorkflowCommon.Concurrency(group, cancelInProgress)
    }

    fun jobs(fn: JobsBuilder.() -> Unit) {
        jb.fn()
    }

    fun env(fn: EnvBuilder.() -> Unit) {
        eb.fn()
    }

    internal fun build(): ReusableWorkflow {
        val workflowCall = wfcb.build()
        return ReusableWorkflow(
            name,
            ReusableWorkflow.ReusableWorkflowTrigger(ReusableWorkflow.ReusableWorkflowTrigger.WorkflowCall(workflowCall.first, workflowCall.second)),
            concurrency,
            eb.build().ifEmpty { null },
            jb.build()
        )
    }

    class WorkflowCallBuilder() {
        private val inputsMap = LinkedHashMap<GitHubActionInputParameter, Input>()
        private val outputsMap = LinkedHashMap<GitHubActionOutputParameter, WorkflowCommon.Output>()

        infix fun GitHubActionInputParameter.to(input: Input) {
            inputsMap[this] = input
        }

        fun output(parameter: GitHubActionOutputParameter, output: WorkflowCommon.Output) {
            outputsMap[parameter] = output
        }

        fun output(parameter: GitHubActionOutputParameter, jobID: WorkflowCommon.JobID, description: String) {
            outputsMap[parameter] = WorkflowCommon.Output(description, outputRef(jobID, parameter))
        }

        fun inputChoice(name: GitHubActionInputParameter, description: String, options: List<String>, default: String, required: Boolean = false) {
            name to Input.Choice(description, options, default, required)
        }

        fun inputBoolean(name: GitHubActionInputParameter, description: String, default: Boolean? = null, required: Boolean = false) {
            name to Input.Boolean(description, default, required)
        }

        fun inputString(name: GitHubActionInputParameter, description: String, default: String? = null, required: Boolean = false) {
            name to Input.String(description, default, required)
        }

        fun build(): Pair<Map<GitHubActionInputParameter, Input>, Map<GitHubActionOutputParameter, WorkflowCommon.Output>> = inputsMap.toMap() to outputsMap.toMap()
    }
}

class TriggerBuilder() {
    private val pb: PushBuilder = PushBuilder()
    private val prb: PullRequestBuilder = PullRequestBuilder()
    private val wfdb: WorkflowDispatchBuilder = WorkflowDispatchBuilder()
    private val sb: ScheduleBuilder = ScheduleBuilder()

    fun workflowDispatch(fn: WorkflowDispatchBuilder.() -> Unit) {
        wfdb.fn()
    }

    fun push(fn: PushBuilder.() -> Unit) {
        pb.fn()
    }

    fun pullRequest(fn: PullRequestBuilder.() -> Unit) {
        prb.fn()
    }

    fun schedule(fn: ScheduleBuilder.() -> Unit) {
        sb.fn()
    }

    internal fun build(): Triggers {
        val wfd = wfdb.build()
        return Triggers(
            if (wfd.isEmpty()) null else Trigger.WorkflowDispatch(wfd),
            pb.build(),
            prb.build(),
            sb.build()
        )
    }
}

class WorkflowDispatchBuilder() {
    private val map = LinkedHashMap<GitHubActionInputParameter, Input>()

    infix fun GitHubActionInputParameter.to (input: Input) {
        map[this] = input
    }

    fun inputChoice(name: GitHubActionInputParameter, description: String, options: List<String>, default: String, required: Boolean = false) {
        name to Input.Choice(description, options, default, required)
    }

    fun inputBoolean(name: GitHubActionInputParameter, description: String, default: Boolean? = null, required: Boolean = false) {
        name to Input.Boolean(description, default, required)
    }

    fun inputString(name: GitHubActionInputParameter, description: String, default: String? = null, required: Boolean = false) {
        name to Input.String(description, default, required)
    }

    fun build(): Map<GitHubActionInputParameter, Input> = map.toMap()
}

class JobsBuilder {
    private val map = LinkedHashMap<WorkflowCommon.JobID, Job>()

    infix fun String.to (job: Job) {
        map[WorkflowCommon.JobID(this)] = job
    }

    infix fun WorkflowCommon.JobID.to (job: Job) {
        map[this] = job
    }

    fun stepID(id: String) = Step.StepID(id)

    fun build(): LinkedHashMap<WorkflowCommon.JobID, Job> = map
}

class PushBuilder {
    var branches: List<String> = emptyList()
    var branchesIgnore: List<String> = emptyList()
    var tags: List<String> = emptyList()
    var tagsIgnore: List<String> = emptyList()
    var pathsIgnore: List<String> = emptyList()
    
    internal fun build(): Trigger.Push? {
        return if (branches.isEmpty() && branchesIgnore.isEmpty() && tags.isEmpty() && tagsIgnore.isEmpty() && pathsIgnore.isEmpty())
            null
        else
            Trigger.Push(
                Collections.unmodifiableList(branches),
                Collections.unmodifiableList(tags),
                Collections.unmodifiableList(branchesIgnore),
                Collections.unmodifiableList(tagsIgnore),
                Collections.unmodifiableList(pathsIgnore),
            )
    }
}

class PullRequestBuilder {
    var branches: List<String> = emptyList()
    var branchesIgnore: List<String> = emptyList()
    var pathsIgnore: List<String> = emptyList()

    internal fun build(): Trigger.PullRequest? {
        return if (branches.isEmpty() && branchesIgnore.isEmpty() && pathsIgnore.isEmpty())
            null
        else
            Trigger.PullRequest(
                Collections.unmodifiableList(branches),
                Collections.unmodifiableList(branchesIgnore),
                Collections.unmodifiableList(pathsIgnore),
            )
    }
}

class ScheduleBuilder {
    private var crons: List<Trigger.Cron> = emptyList()
    private val cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX)
    private val cronParser = CronParser(cronDefinition)

    fun cron(expression: String) {
        val sanitizedExpression = expression.trim()
        cronParser.parse(sanitizedExpression)
        crons = crons + Trigger.Cron(if (expression.startsWith("*")) expression else " $expression")
    }

    internal fun build(): List<Trigger.Cron>? {
        return crons.ifEmpty { null }
    }
}

fun workflow(
    name: String,
    fn: WorkflowBuilder.() -> Unit,
): Workflow {
    val wfb = WorkflowBuilder(name)
    wfb.fn()
    return wfb.build()
}

fun reusableWorkflow(
    name: String,
    fn: ReusableWorkflowBuilder.() -> Unit,
): ReusableWorkflow {
    val wfb = ReusableWorkflowBuilder(name)
    wfb.fn()
    return wfb.build()
}