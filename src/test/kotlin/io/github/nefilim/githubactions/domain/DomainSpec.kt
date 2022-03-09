package io.github.nefilim.githubactions.domain

import io.github.nefilim.githubactions.GitHubActionsYAML
import io.github.nefilim.githubactions.actions.GradleBuildAction
import io.github.nefilim.githubactions.domain.WorkflowCommon.Job.Step
import io.github.nefilim.githubactions.domain.WorkflowCommon.Input
import io.github.nefilim.githubactions.domain.Workflow.Triggers
import io.github.nefilim.githubactions.domain.Workflow.Triggers.Trigger
import io.github.nefilim.githubactions.dsl.reusableWorkflow
import io.github.nefilim.githubactions.outputRef
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class DomainSpec: WordSpec() {

    init {
        "Steps" should {
            "produce a GradleBuildAction step" {
                val step = GradleBuildAction(
                    buildRootDirectory = "sourceDir",
                    arguments = "clean build"
                ).toStep()
                GitHubActionsYAML.encodeToString(Step.serializer(), step) shouldBe """
                    name: 'Gradle Build'
                    uses: 'gradle/gradle-build-action@v2'
                    with:
                      'arguments': 'clean build'
                      'build-root-directory': 'sourceDir'
                    id: 'gradle-build-action'
                """.trimIndent()
            }
        }

        "Triggers" should {
            "produce a Push Trigger" {
                val trigger = Trigger.Push(
                    branches = listOf("main"),
                    tags = listOf("v2"),
                    branchesIgnore = listOf("bla"),
                    pathsIgnore = listOf("**.md"),
                )
                GitHubActionsYAML.encodeToString(Trigger.Push.serializer(), trigger) shouldBe """
                    branches:
                    - 'main'
                    tags:
                    - 'v2'
                    branches-ignore:
                    - 'bla'
                    paths-ignore:
                    - '**.md'
                """.trimIndent()
            }

            "produce Triggers" {
                val trigger = Trigger.PullRequest(
                    branches = listOf("main", "develop"),
                    branchesIgnore = listOf("bla"),
                    pathsIgnore = listOf("**.md"),
                )
                GitHubActionsYAML.encodeToString(Triggers.serializer(), Triggers(pullRequest = trigger)) shouldBe """
                    pull_request:
                      branches:
                      - 'main'
                      - 'develop'
                      branches-ignore:
                      - 'bla'
                      paths-ignore:
                      - '**.md'
                """.trimIndent()
            }
        }

        "Trigger Inputs" should {
            "produce a String input" {
                Trigger.WorkflowDispatch(
                    mapOf(
                        AdhocInputParameter("the_string") to Input.String("cool string input", "hello world", required = true),
                        AdhocInputParameter("the_bool") to Input.Boolean("cool bool input", true, required = true),
                        AdhocInputParameter("pick_one") to Input.Choice("cool input", listOf("A", "B", "C"), default = "B", required = true),
                    )
                ).also {
                    GitHubActionsYAML.encodeToString(Trigger.WorkflowDispatch.serializer(), it) shouldBe """
                        inputs:
                          'the_string':
                            description: 'cool string input'
                            default: 'hello world'
                            required: true
                            type: 'string'
                          'the_bool':
                            description: 'cool bool input'
                            default: true
                            required: true
                            type: 'boolean'
                          'pick_one':
                            description: 'cool input'
                            options:
                            - 'A'
                            - 'B'
                            - 'C'
                            default: 'B'
                            required: true
                            type: 'choice'
                    """.trimIndent()
                }
            }
        }

        "Reusable Workflow" should {
            "produce a valid workflow" {
                val stepID = Step.StepID("step-name-id")
                val jobID = WorkflowCommon.JobID("ci-build-job")
                val outputParameter = AdhocOutputParameter("version")
                reusableWorkflow("test") {
                    workflowCall {
                        inputString(AdhocInputParameter("versionModifier"), "the semver modifier", "patch", true)
                        output(outputParameter, jobID,"the sem version")
                    }
                    jobs {
                        jobID to WorkflowCommon.Job(
                            runsOn = listOf("large"),
                            steps = listOf(
                                Step.Uses(
                                    "step-name",
                                    uses = "actions/checkout@v2",
                                    id = stepID,
                                    parameters = mapOf(
                                        AdhocInputParameter("path") to "src"
                                    )
                                )
                            ),
                            outputs = mapOf(
                                outputParameter to outputRef(stepID, outputParameter)
                            )
                        )
                    }
                    env {
                        "DEPLOYMENT_CLUSTER" to "production"
                    }
                }.also {
                    GitHubActionsYAML.encodeToString(ReusableWorkflow.serializer(), it).also { println(it) } shouldBe """
                        name: 'test'
                        on:
                          workflow_call:
                            inputs:
                              'versionModifier':
                                description: 'the semver modifier'
                                default: 'patch'
                                required: true
                                type: 'string'
                            outputs:
                              'version':
                                description: 'the sem version'
                                value: '${'$'}{{ needs.${jobID.id}.outputs.version }}'
                        env:
                          'DEPLOYMENT_CLUSTER': 'production'
                        jobs:
                          'ci-build-job':
                            runs-on:
                            - 'large'
                            steps:
                            - name: 'step-name'
                              uses: 'actions/checkout@v2'
                              with:
                                'path': 'src'
                              id: 'step-name-id'
                            outputs:
                              'version': '${'$'}{{ steps.step-name-id.outputs.version }}'
                    """.trimIndent()
                }
            }
        }
    }
}