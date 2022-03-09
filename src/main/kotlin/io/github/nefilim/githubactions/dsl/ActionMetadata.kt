@file:OptIn(ExperimentalSerializationApi::class)

package io.github.nefilim.githubactions.dsl

import io.github.nefilim.githubactions.GithubActionMetadataDSL
import io.github.nefilim.githubactions.domain.CompositeAction
import io.github.nefilim.githubactions.domain.GitHubActionInputParameter
import io.github.nefilim.githubactions.domain.GitHubActionOutputParameter
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*
import kotlin.collections.LinkedHashMap

@GithubActionMetadataDSL
class CompositeActionMetadataBuilder(
    private val name: String,
    private val description: String,
    private val author: String? = null,
) {
    private val ib = InputsBuilder()
    private val ob = OutputsBuilder()
    private val rb = RunsBuilder()
    private val eb = EnvBuilder()

    fun outputs(fn: OutputsBuilder.() -> Unit) {
        ob.fn()
    }

    fun inputs(fn: InputsBuilder.() -> Unit) {
        ib.fn()
    }

    fun runs(fn: RunsBuilder.() -> Unit) {
        rb.fn()
    }

    internal fun build(): CompositeAction {
        return CompositeAction(
            name,
            description,
            author,
            ib.build().ifEmpty { null },
            ob.build().ifEmpty { null },
            rb.build()
        )
    }
}

class InputsBuilder() {
    private val map = LinkedHashMap<GitHubActionInputParameter, CompositeAction.Input>()

    infix fun GitHubActionInputParameter.to (input: CompositeAction.Input) {
        map[this] = input
    }

    fun build(): Map<GitHubActionInputParameter, CompositeAction.Input> = map.toMap()
}

class OutputsBuilder() {
    private val map = LinkedHashMap<GitHubActionOutputParameter, CompositeAction.Output>()

    infix fun GitHubActionOutputParameter.to (input: CompositeAction.Output) {
        map[this] = input
    }

    fun build(): Map<GitHubActionOutputParameter, CompositeAction.Output> = map.toMap()
}

class RunsBuilder() {
    var steps: List<CompositeAction.Runs.Step> = emptyList()

    fun build(): CompositeAction.Runs = CompositeAction.Runs("composite", Collections.unmodifiableList(steps))
}

class RunsStepsBuilder() {
    private val map = LinkedHashMap<String, CompositeAction.Runs.Step>()

    infix fun String.to (input: CompositeAction.Runs.Step) {
        map[this] = input
    }

    fun build(): Map<String, CompositeAction.Runs.Step> = map.toMap()
}

fun compositeAction(
    name: String,
    description: String,
    fn: CompositeActionMetadataBuilder.() -> Unit,
): CompositeAction {
    val camb = CompositeActionMetadataBuilder(name, description)
    camb.fn()
    return camb.build()
}

fun compositeAction(
    name: String,
    description: String,
    author: String,
    fn: CompositeActionMetadataBuilder.() -> Unit,
): CompositeAction {
    val camb = CompositeActionMetadataBuilder(name, description, author)
    camb.fn()
    return camb.build()
}