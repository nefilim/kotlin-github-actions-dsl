package io.github.nefilim.githubactions.actions

import io.github.nefilim.githubactions.domain.Workflow.Job.Step
import io.github.nefilim.githubactions.outputRef
import io.github.nefilim.githubactions.param

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
): GitHubAction {
    override val name: String = "Setup Java JDK"
    override val description: String = "Set up a specific version of the Java JDK and add the command-line tools to the PATH"

    enum class InputParameter(override val parameter: String): GitHubActionInputParameter {
        JavaVersion("java-version"),
        Distribution("distribution"),
        JavaPackage("java-package"),
        Architecture("architecture"),
        JdkFile("jdk-file"),
        CheckLatest("check-latest"),
        ServerID("server-id"),
        ServerUsername("server-username"),
        ServerPassword("server-password"),
        SettingsPath("settings-path"),
        OverwriteSettings("overwrite-settings"),
        GPGPrivateKey("gpg-private-key"),
        GPGPassphrase("gpg-passphrase"),
        Cache("cache"),
        JobStatus("job-status"),
    }

    enum class OutputParameter(override val parameter: String): GitHubActionOutputParameter {
        Distribution("distribution"), // Distribution of Java that has been installed
        Version("version"), // Actual version of the java environment that has been installed
        Path("path"), // Path to where the java environment has been installed (same as $JAVA_HOME)
    }
    
    companion object {
        const val Uses: String = "actions/setup-java@v2"
        val DefaultStepID = Step.WStepID("actions-setup-java")
    }

    override fun toStep(id: Step.WStepID, name: String, uses: String): Step {
        return Step.Uses(name, uses, id,
            // preserve parameter order
            linkedMapOf(
                *listOfNotNull(
                    param(InputParameter.JavaVersion, javaVersion),
                    param(InputParameter.Distribution, distribution),
                    javaPackage?.let { param(InputParameter.JavaPackage, it) },
                    architecture?.let { param(InputParameter.Architecture, it) },
                    jdkFile?.let { param(InputParameter.JdkFile, it) },
                    checkLatest?.let { param(InputParameter.CheckLatest, it) },
                    serverID?.let { param(InputParameter.ServerID, it) },
                    serverUsername?.let { param(InputParameter.ServerUsername, it) },
                    serverPassword?.let { param(InputParameter.ServerPassword, it) },
                    settingsPath?.let { param(InputParameter.SettingsPath, it) },
                    overwriteSettings?.let { param(InputParameter.OverwriteSettings, it) },
                    gpgPrivateKey?.let { param(InputParameter.GPGPrivateKey, it) },
                    gpgPassphrase?.let { param(InputParameter.GPGPassphrase, it) },
                    cache?.let { param(InputParameter.Cache, it) },
                    jobStatus?.let { param(InputParameter.JobStatus, it) },
                ).toTypedArray()
            ),
            mapOf(
                OutputParameter.Distribution to outputRef(id, OutputParameter.Distribution),
                OutputParameter.Version to outputRef(id, OutputParameter.Version),
                OutputParameter.Path to outputRef(id, OutputParameter.Path),
            )
        )
    }

    fun toStep(): Step = toStep(DefaultStepID, "Setup Java", Uses)
}