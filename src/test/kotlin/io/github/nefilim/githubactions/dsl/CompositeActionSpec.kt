package io.github.nefilim.githubactions.dsl

import io.github.nefilim.githubactions.GitHubActionsYAML
import io.github.nefilim.githubactions.actionInputRef
import io.github.nefilim.githubactions.actions.CheckoutAction
import io.github.nefilim.githubactions.domain.CompositeAction
import io.github.nefilim.githubactions.domain.adhoc
import io.github.nefilim.githubactions.expression
import io.kotest.core.spec.style.WordSpec

class CompositeActionSpec: WordSpec() {
    init {
        "CompositeActionSpec" should {
            "generate" {
                compositeAction(
                    "cd-deploy-action",
                          "Deploy Docker Container",
                ) {
                    inputs {
                        "sourcePath" to CompositeAction.Input("Location of the source, leave at default to skip checking out source (ie previously checked out already)", false, "false")
                        "serviceVersion" to CompositeAction.Input("The Semver tag on the Docker Image", true)
                        "deploymentFile" to CompositeAction.Input("The deployment.yaml for the Gradle build", true)
                        "dockerImageName" to CompositeAction.Input("The Docker Image to deploy", true)
                        "garHost" to CompositeAction.Input("Google Artifact Registry host", true)
                        "deployinatorImage" to CompositeAction.Input("Deployinator Docker Image", true, "us-east1-docker.pkg.dev/figure-development/figure-docker-repo/deployinator:latest")
                        "deploymentCluster" to CompositeAction.Input("The cluster where to deploy too", true)
                        "deployinatorScriptLocation" to CompositeAction.Input("The script to pass deployinator container to execute", true, "/data/platform-shared-actions/cd-deploy/deploy-script.sh")
                        "gcpAuthJson" to CompositeAction.Input("The GCP Auth JSON token", true)
                        "deployinatorAuthJson" to CompositeAction.Input("The deployinator Auth JSON token", true)
                    }
                    runs {
                        steps = listOf(
                            CompositeAction.Runs.Step(
                                name = "Check Inputs",
                                shell = "bash",
                                run = listOf(
                                    "echo 'source: ${actionInputRef("sourcePath")}'",
                                    "echo 'service_version: ${actionInputRef("service_version")}'",
                                    "echo 'deployment_file: ${actionInputRef("deploymentFile")}'",
                                    "echo 'docker_image_name: ${actionInputRef("dockerImageName")}'",
                                ).joinToString("\n")
                            ),
                            CompositeAction.Runs.Step(
                                name = "Checkout",
                                predicate = expression("inputs.sourcePath != 'false'"),
                                uses = CheckoutAction.Companion.Uses,
                                parameters = mapOf(
                                    "path" to actionInputRef("sourcePath"),
                                    "fetch-depth" to "0",
                                ).adhoc()
                            ),
                            CompositeAction.Runs.Step(
                                name = "Login to GAR",
                                uses = "docker/login-action@v1",
                                parameters = mapOf(
                                    "registry" to actionInputRef("garHost"),
                                    "username" to "_json_key",
                                    "password" to actionInputRef("gcpAuthJson"),
                                ).adhoc()
                            ),
                            CompositeAction.Runs.Step(
                                name = "Run Deployinator",
                                shell = "bash",
                                env = mapOf(
                                    "DEPLOYMENT_CLUSTER" to actionInputRef("deploymentCluster"),
                                    "DEPLOYINATOR_AUTH_JSON" to actionInputRef("deployinatorAuthJson"),
                                    "DEPLOYMENT_FILE" to actionInputRef("deploymentFile"),
                                    "DEPLOYMENT_DOCKER_IMAGE_NAME" to actionInputRef("dockerImageName"),
                                    "DEPLOYMENT_DOCKER_IMAGE_VERSION" to actionInputRef("serviceVersion"),
                                ),
                                run = """
                                    set -e
                                    docker run \
                                    -e DEPLOYINATOR_AUTH_JSON \
                                    -e DEPLOYMENT_CLUSTER \
                                    -e DEPLOYMENT_DOCKER_IMAGE_NAME \
                                    -e DEPLOYMENT_DOCKER_IMAGE_VERSION \
                                    -e DEPLOYMENT_FILE \
                                    -e GITHUB_ACTIONS \
                                    -e GITHUB_REF \
                                    -e GITHUB_SERVER_URL \
                                    -e GITHUB_REPOSITORY \
                                    -e GITHUB_RUN_ID \
                                    -v `pwd`:/data -w /data ${actionInputRef("deployinatorImage")} sh -c "${actionInputRef("deployinatorScriptLocation")}"
                                """.trimIndent()
                            ),
                        )
                    }

                }.also {
                    println(GitHubActionsYAML.encodeToString(CompositeAction.serializer(), it))
                }
            }
        }
    }
}