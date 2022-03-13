![Kotlin version](https://img.shields.io/badge/kotlin-1.6.0-blueviolet?logo=kotlin&logoColor=white)

# Kotlin DSL for Github Actions 

Simple DSL to generate Github Actions YAML workflows. 

## Usage

Include the following dependencies: 
* `io.github.nefilim.githubactions:kotlin-dsl-core:<latest>` 
* `io.github.nefilim.githubactions:kotlin-dsl-actions:<latest>`

If you wish to generate additional actions from metadata, also include:
* `io.github.nefilim.githubactions:kotlin-dsl-action-generator:<latest>`

Define your workflow, example:

```kotlin
val wf = workflow("CI Build") {
    triggers {
        push {
            branches = listOf("main")
            pathsIgnore = listOf("**.md")
        }
        pullRequest {
            branchesIgnore = listOf("renovate/*")
            pathsIgnore = listOf("**.md")
        }
        workflowDispatch {
            inputString("deploymentFilename", "Deployment descriptor name", "deployment.yaml", false)
        }
    }
    concurrency("ci-build-${githubRef("ref")}", true)
    env {
        "NEXUS_USER" to secretRef("NEXUS_USER")
        "NEXUS_PASS" to secretRef("NEXUS_PASS")
    }
    jobs {
        "ci-build" to Job(
            runsOn = listOf("linux", "self-hosted"),
            steps = listOf(
                CheckoutAction(
                    path = "source",
                    repository = "nefilim/gradle-github-actions-generator",
                    ref = "main",
                ).toStep(Step.StepID("my-checkout"), "Checkout Source", CheckoutAction.Uses),
                GradleBuildAction(
                    buildRootDirectory = "source",
                    arguments = "clean build"
                ).toStep()
            ),
        )
    }
}
```

Generate the corresponding YAML using [KotlinX Serialization](https://github.com/Kotlin/kotlinx.serialization) & [YAML](https://github.com/charleskorn/kaml):

```kotlin
    println(GitHubActionsYAML.encodeToString(Workflow.serializer(), wf))
```

A Gradle Plugin is also available to generate workflows right from your build definition: https://github.com/nefilim/gradle-github-actions-generator
                               
## Bundled Actions

Note: these are actions are generated and not committed to the source tree:

* [CheckoutActionV3](https://github.com/actions/checkout)
* [SetupJavaActionV3](https://github.com/actions/setup-java)
* [GradleBuildActionV2](https://github.com/gradle/gradle-build-action)

To add additional type safe GitHub Actions, please implement the [GithubAction interface](https://github.com/nefilim/kotlin-github-actions-dsl/blob/main/core/src/main/kotlin/io/github/nefilim/githubactions/domain/GitHubAction.kt) or use the code generator, an example can be seen [here](https://github.com/nefilim/kotlin-github-actions-dsl/blob/main/action-generator/src/main/kotlin/io/github/nefilim/githubactions/generator/GitHubActionGenerator.kt#L212-L221). 
