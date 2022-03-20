package io.github.nefilim.githubactions.generator

const val PackageName = "io.github.nefilim.githubactions.generated"

val BundledActions = listOf(
    ActionToGenerate("actions", "checkout", "v3"),
    ActionToGenerate("actions", "setup-java", "v3"),
    ActionToGenerate("gradle", "gradle-build-action", "v2"),
    ActionToGenerate("gradle", "wrapper-validation-action", "v1"),
    ActionToGenerate("slackapi", "slack-github-action", "v1"),
    // Docker
    ActionToGenerate("docker", "build-push-action", "v2"),
    ActionToGenerate("docker", "login-action", "v1"),
    ActionToGenerate("docker", "setup-buildx-action", "v1"),
    ActionToGenerate("docker", "metadata-action", "v3"),
    // Google
    ActionToGenerate("google-github-actions", "setup-gcloud", "v0"),
    ActionToGenerate("google-github-actions", "auth", "v0"),
    ActionToGenerate("google-github-actions", "deploy-cloudrun", "v0"),
    ActionToGenerate("google-github-actions", "upload-cloud-storage", "v0"),
    ActionToGenerate("google-github-actions", "deploy-appengine", "v0"),
    ActionToGenerate("google-github-actions", "get-secretmanager-secrets", "v0"),
)