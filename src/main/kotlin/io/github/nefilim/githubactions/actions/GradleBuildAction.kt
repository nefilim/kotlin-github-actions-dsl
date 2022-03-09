package io.github.nefilim.githubactions.actions

import io.github.nefilim.githubactions.domain.Environment
import io.github.nefilim.githubactions.domain.GitHubActionInputParameter
import io.github.nefilim.githubactions.domain.GitHubActionOutputParameter
import io.github.nefilim.githubactions.domain.WorkflowCommon.Job.Step
import io.github.nefilim.githubactions.expression
import io.github.nefilim.githubactions.inputRef
import io.github.nefilim.githubactions.outputRef
import io.github.nefilim.githubactions.param

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
): GitHubAction {
    override val name: String = "Gradle Build Action"
    override val description: String = "Configures Gradle for use in GitHub actions, caching useful state in the GitHub actions cache"

    enum class InputParameter(override val parameter: String): GitHubActionInputParameter {
        GradleVersion("gradle-version"),
        CacheDisabled("cache-disabled"),
        CacheReadOnly("cache-read-only"),
        GradleHomeCacheIncludes("gradle-home-cache-includes"),
        GradleHomeCacheExcludes("gradle-home-cache-excludes"),
        Arguments("arguments"),
        BuildRootDirectory("build-root-directory"),
        GradleExecutable("gradle-executable"),
        CacheWriteOnly("cache-write-only"),
        GradleHomeCacheStrictMatch("gradle-home-cache-strict-match"),
        WorkflowJobContext("workflow-job-context"),
    }

    enum class OutputParameter(override val parameter: String): GitHubActionOutputParameter {
        BuildScanURL("build-scan-url")
    }

    companion object {
        const val Uses: String = "gradle/gradle-build-action@v2"
        val DefaultStepID = Step.StepID("gradle-build-action")

        fun cacheReadOnlyFromBranch(defaultBranch: GitHubActionInputParameter): String =
            cacheReadOnlyFromBranch(inputRef(defaultBranch))
        fun cacheReadOnlyFromBranch(defaultBranch: String): String =
            expression("!(github.ref == 'refs/heads/$defaultBranch') || github.head_ref == '$defaultBranch'")
    }

    override fun toStep(id: Step.StepID, name: String, uses: String, predicate: String?, env: Environment?): Step {
        return Step.Uses(name, uses, id,
            // preserve parameter order
            linkedMapOf(
                *listOfNotNull(
                    gradleVersion?.let { param(InputParameter.GradleVersion, it) },
                    cacheDisabled?.let { param(InputParameter.CacheDisabled, it) },
                    cacheReadOnly?.let { param(InputParameter.CacheReadOnly, it) },
                    gradleHomeCacheIncludes?.let { param(InputParameter.GradleHomeCacheIncludes, it) },
                    gradleHomeCacheExcludes?.let { param(InputParameter.GradleHomeCacheExcludes, it) },
                    arguments?.let { param(InputParameter.Arguments, it) },
                    buildRootDirectory?.let { param(InputParameter.BuildRootDirectory, it) },
                    gradleExecutable?.let { param(InputParameter.GradleExecutable, it) },
                    cacheWriteOnly?.let { param(InputParameter.CacheWriteOnly, it) },
                    gradleHomeCacheStrictMatch?.let { param(InputParameter.GradleHomeCacheStrictMatch, it) },
                    workflowJobContext?.let { param(InputParameter.WorkflowJobContext, it) },
                ).toTypedArray()
            ),
            predicate,
            env,
            mapOf(
                OutputParameter.BuildScanURL to outputRef(id, OutputParameter.BuildScanURL),
            )
        )
    }
    
    fun toStep(): Step = toStep(DefaultStepID, "Gradle Build", Uses, null, null)
}