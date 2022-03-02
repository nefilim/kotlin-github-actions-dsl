@file:OptIn(ExperimentalSerializationApi::class)

package io.github.nefilim.githubactions.dsl

import io.github.nefilim.githubactions.dsl.Trigger.WorkflowDispatch.Input
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.Collections

@GithubActionsDSL
class WorkflowBuilder(
    private val name: String,
) {
    private var concurrency: String? = null
    private val tb = TriggerBuilder()
    private val jb = JobsBuilder()
    private val eb = EnvBuilder()

    fun triggers(fn: TriggerBuilder.() -> Unit) {
        tb.fn()
    }

    fun concurrency(con: String) {
        this.concurrency = con
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

class TriggerBuilder() {
    private val pb: PushBuilder = PushBuilder()
    private val prb: PullRequestBuilder = PullRequestBuilder()
    private val wfdb: WorkflowDispatchBuilder = WorkflowDispatchBuilder()

    fun workflowDispatch(fn: WorkflowDispatchBuilder.() -> Unit) {
        wfdb.fn()
    }

    fun push(fn: PushBuilder.() -> Unit) {
        pb.fn()
    }

    fun pullRequest(fn: PullRequestBuilder.() -> Unit) {
        prb.fn()
    }

    internal fun build(): Triggers {
        val wfd = wfdb.build()
        return Triggers(
            if (wfd.isEmpty()) null else Trigger.WorkflowDispatch(wfd),
            pb.build(),
            prb.build(),
        )
    }
}

class WorkflowDispatchBuilder() {
    private val map = LinkedHashMap<String, Input>()

    infix fun String.to (input: Input) {
        map[this] = input
    }

    fun inputChoice(name: String, description: String, options: List<String>, default: String, required: Boolean = false) {
        name to Input.Choice(description, options, default, required)
    }

    fun inputBoolean(name: String, description: String, default: Boolean? = null, required: Boolean = false) {
        name to Input.Boolean(description, default, required)
    }

    fun inputString(name: String, description: String, default: String? = null, required: Boolean = false) {
        name to Input.String(description, default, required)
    }

    fun build(): Map<String, Input> = map.toMap()
}

class EnvBuilder() {
    private val map = HashMap<String, String>()

    infix fun String.to (value: String) {
        map[this] = value
    }

    fun build(): Environment = map.toMap()
}


class JobsBuilder {
    private val map = HashMap<String, Job>()

    infix fun String.to (job: Job) {
        map[this] = job
    }

    fun stepID(id: String) = Step.StepID(id)

    fun build(): Map<String, Job> = map.toMap()
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

fun workflow(
    name: String,
    fn: WorkflowBuilder.() -> Unit,
): Workflow {
    val wfb = WorkflowBuilder(name)
    wfb.fn()
    return wfb.build()
}