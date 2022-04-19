package io.github.nefilim.githubactions.dsl

import io.github.nefilim.githubactions.GitHubActionsYAML
import io.github.nefilim.githubactions.domain.Workflow
import io.kotest.core.spec.style.WordSpec

class WorkflowSpec: WordSpec() {
    init {
        "Workflow" should {
            "generate schedule triggers" {
                workflow(
                    "test-workflow",
                ) {
                    triggers {
                        schedule {
                            cron("2 5,17 * * *")
                            cron("*/5 5,17 * * *")
                        }
                    }
                }.also {
                    println(GitHubActionsYAML.encodeToString(Workflow.serializer(), it))
                }
            }
        }
    }
}