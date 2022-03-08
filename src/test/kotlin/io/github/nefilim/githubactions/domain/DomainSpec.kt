package io.github.nefilim.githubactions.domain

import io.github.nefilim.githubactions.GitHubActionsYAML
import io.github.nefilim.githubactions.actions.GradleBuildAction
import io.github.nefilim.githubactions.domain.Workflow.Job.Step
import io.github.nefilim.githubactions.domain.Workflow.Triggers
import io.github.nefilim.githubactions.domain.Workflow.Triggers.Trigger
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
                        "the_string" to Trigger.WorkflowDispatch.Input.String("cool string input", "hello world", required = true),
                        "the_bool" to Trigger.WorkflowDispatch.Input.Boolean("cool bool input", true, required = true),
                        "pick_one" to Trigger.WorkflowDispatch.Input.Choice("cool input", listOf("A", "B", "C"), default = "B", required = true),
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
    }
}