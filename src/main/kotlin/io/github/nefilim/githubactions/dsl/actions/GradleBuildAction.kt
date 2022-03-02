package io.github.nefilim.githubactions.dsl.actions

import io.github.nefilim.githubactions.dsl.Step
import io.github.nefilim.githubactions.dsl.outputRef
import io.github.nefilim.githubactions.dsl.param

// TODO code gen this from parsing action.yaml
data class GradleBuildAction(
    val gradleVersion: String? = null,
    val cacheDisabled: String? = null,
    val cacheReadOnly: String? = null,
    val gradleHomeCacheIncludes: String? = null,
    val gradleHomeCacheExcludes: String? = null,
    val arguments: String? = null,
    val buildRootDirectory: String? = null,
    val gradleExecutable: String? = null,
    val cacheWriteOnly: String? = null,
    val gradleHomeCacheStrictMatch: String? = null,
    val workflowJobContext: String? = null,
): GithubAction {
    override val name: String = "Gradle Build Action"
    override val description: String = "Configures Gradle for use in GitHub actions, caching useful state in the GitHub actions cache"

    companion object {
        const val Uses: String = "gradle/gradle-build-action@v2"
        val DefaultStepID = Step.StepID("gradle-build-action")

        object Outputs {
            const val BuildScanURL = "build-scan-url"
        }
    }

    override fun toStep(id: Step.StepID, name: String, uses: String): Step {
        return Step.Uses(name, uses, id,
            // preserve parameter order
            linkedMapOf(
                *listOfNotNull(
                    gradleVersion?.let { param("gradle-version", it) },
                    cacheDisabled?.let { param("cache-disabled", it) },
                    cacheReadOnly?.let { param("cache-read-only", it) },
                    gradleHomeCacheIncludes?.let { param("gradle-home-cache-includes", it) },
                    gradleHomeCacheExcludes?.let { param("gradle-home-cache-excludes", it) },
                    arguments?.let { param("arguments", it) },
                    buildRootDirectory?.let { param("build-root-directory", it) },
                    gradleExecutable?.let { param("gradle-executable", it) },
                    cacheWriteOnly?.let { param("cache-write-only", it) },
                    gradleHomeCacheStrictMatch?.let { param("gradle-home-cache-strict-match", it) },
                    workflowJobContext?.let { param("workflow-job-context", it) },
                ).toTypedArray()
            ),
            mapOf(
                Outputs.BuildScanURL to outputRef(id, Outputs.BuildScanURL),
            )
        )
    }
    
    fun toStep(): Step = toStep(DefaultStepID, "Gradle Build", Uses)
}