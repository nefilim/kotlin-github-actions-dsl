@file:OptIn(ExperimentalSerializationApi::class)

package io.github.nefilim.kghadsl

import io.github.nefilim.kghadsl.Trigger.WorkflowDispatch.Companion.Input
import kotlinx.serialization.ExperimentalSerializationApi

@Annotations
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
    private var wfdb: WorkflowDispatchBuilder = WorkflowDispatchBuilder()

    fun workflowDispatch(fn: WorkflowDispatchBuilder.() -> Unit) {
        wfdb.fn()
    }

    fun push(fn: PushBuilder.() -> Unit) {
        pb.fn()
    }

    internal fun build(): Triggers {
        val wfd = wfdb.build()
        return Triggers(
            if (wfd.isEmpty()) null else Trigger.WorkflowDispatch(wfd),
            pb.build()
        )
    }
}

class WorkflowDispatchBuilder() {
    private var map = LinkedHashMap<String, Input>()

    infix fun String.to (input: Input) {
        map[this] = input
    }

    fun inputChoice(description: String, options: List<String>, default: String): Input.Choice =
        Trigger.WorkflowDispatch.Companion.Input.Choice(description, options, default)

    fun inputString(description: String, default: String? = null, required: Boolean = false): Input.String =
        Trigger.WorkflowDispatch.Companion.Input.String(description, default, required)

    fun build(): Map<String, Input> = map.toMap()
}

class EnvBuilder() {
    private var map = HashMap<String, String>()

    infix fun String.to (value: String) {
        map[this] = value
    }

    fun build(): Environment = map.toMap()
}


class JobsBuilder {
    private var map = HashMap<String, Job>()

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
    
    internal fun build(): Trigger.Push? {
        return if (branches.isEmpty() && branchesIgnore.isEmpty() && tags.isEmpty() && tagsIgnore.isEmpty())
            null
        else
            Trigger.Push(
                branches,
                tags,
                branchesIgnore,
                tagsIgnore
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