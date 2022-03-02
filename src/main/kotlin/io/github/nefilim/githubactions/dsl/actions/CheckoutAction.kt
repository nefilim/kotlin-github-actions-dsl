package io.github.nefilim.githubactions.dsl.actions

import io.github.nefilim.githubactions.dsl.Step
import io.github.nefilim.githubactions.dsl.param

// TODO code gen this from parsing action.yaml
data class CheckoutAction(
    val repository: String? = null,
    val ref: String? = null,
    val token: String? = null,
    val sshKey: String? = null,
    val sshKnownHosts: String? = null,
    val sshStrict: String? = null,
    val persistCredentials: String? = null,
    val path: String? = null,
    val clean: String? = null,
    val fetchDepth: String? = null,
    val lfs: String? = null,
    val submodules: String? = null,
): GithubAction {
    override val name: String = "Checkout"
    override val description: String = "Checkout a Git repository at a particular version"

    companion object {
        const val Uses: String = "actions/checkout@v2"
        val DefaultStepID = Step.StepID("actions-checkout")
    }

    override fun toStep(id: Step.StepID, name: String, uses: String): Step {
        return Step.Uses(name, uses, id,
            // preserve parameter order
            linkedMapOf(
                *listOfNotNull(
                    repository?.let { param("repository", it) },
                    ref?.let { param("ref", it) },
                    token?.let { param("token", it) },
                    sshKey?.let { param("ssh-key", it) },
                    sshKnownHosts?.let { param("ssh-known-hosts", it) },
                    sshStrict?.let { param("ssh-strict", it) },
                    persistCredentials?.let { param("persist-credentials", it) },
                    path?.let { param("path", it) },
                    clean?.let { param("clean", it) },
                    fetchDepth?.let { param("fetch-depth", it) },
                    lfs?.let { param("lfs", it) },
                    submodules?.let { param("submodules", it) },
                ).toTypedArray()
            )
        )
    }

    fun toStep(): Step = toStep(DefaultStepID, "Git Checkout", Uses)
}