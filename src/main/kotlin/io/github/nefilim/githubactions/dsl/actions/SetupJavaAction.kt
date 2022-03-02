package io.github.nefilim.githubactions.dsl.actions

import io.github.nefilim.githubactions.dsl.Step
import io.github.nefilim.githubactions.dsl.outputRef
import io.github.nefilim.githubactions.dsl.param

// TODO code gen this from parsing action.yaml
data class SetupJavaAction(
    val javaVersion: String,
    val distribution: String,
    val javaPackage: String? = null,
    val architecture: String? = null,
    val jdkFile: String? = null,
    val checkLatest: String? = null,
    val serverID: String? = null,
    val serverUsername: String? = null,
    val serverPassword: String? = null,
    val settingsPath: String? = null,
    val overwriteSettings: String? = null,
    val gpgPrivateKey: String? = null,
    val gpgPassphrase: String? = null,
    val cache: String? = null,
    val jobStatus: String? = null,
): GithubAction {
    override val name: String = "Setup Java JDK"
    override val description: String = "Set up a specific version of the Java JDK and add the command-line tools to the PATH"

    companion object {
        const val Uses: String = "actions/setup-java@v2"
        val DefaultStepID = Step.StepID("actions-setup-java")

        object Outputs {
            const val Distribution = "distribution" // Distribution of Java that has been installed
            const val Version = "version" // Actual version of the java environment that has been installed
            const val Path = "path" // Path to where the java environment has been installed (same as $JAVA_HOME)
        }
    }

    override fun toStep(id: Step.StepID, name: String, uses: String): Step {
        return Step.Uses(name, uses, id,
            // preserve parameter order
            linkedMapOf(
                *listOfNotNull(
                    param("java-version", javaVersion),
                    param("distribution", distribution),
                    javaPackage?.let { param("java-package", it) },
                    architecture?.let { param("architecture", it) },
                    jdkFile?.let { param("jdk-file", it) },
                    checkLatest?.let { param("check-latest", it) },
                    serverID?.let { param("server-id", it) },
                    serverUsername?.let { param("server-username", it) },
                    serverPassword?.let { param("server-password", it) },
                    settingsPath?.let { param("settings-path", it) },
                    overwriteSettings?.let { param("overwrite-settings", it) },
                    gpgPrivateKey?.let { param("gpg-private-key", it) },
                    gpgPassphrase?.let { param("gpg-passphrase", it) },
                    cache?.let { param("cache", it) },
                    jobStatus?.let { param("job-status", it) },
                ).toTypedArray()
            ),
            mapOf(
                Outputs.Distribution to outputRef(id, Outputs.Distribution),
                Outputs.Version to outputRef(id, Outputs.Version),
                Outputs.Path to outputRef(id, Outputs.Path),
            )
        )
    }

    fun toStep(): Step = toStep(DefaultStepID, "Setup Java", Uses)
}