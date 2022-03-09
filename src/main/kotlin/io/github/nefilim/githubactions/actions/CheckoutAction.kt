package io.github.nefilim.githubactions.actions

import io.github.nefilim.githubactions.domain.Environment
import io.github.nefilim.githubactions.domain.GitHubActionInputParameter
import io.github.nefilim.githubactions.domain.WorkflowCommon.Job.Step
import io.github.nefilim.githubactions.param

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
): GitHubAction {
    override val name: String = "Checkout"
    override val description: String = "Checkout a Git repository at a particular version"

    enum class InputParameter(override val parameter: String): GitHubActionInputParameter {
        Repository("repository"),    
        Ref("ref"),    
        Token("token"),    
        SSHKey("ssh-key"),    
        SSHKnownHosts("ssh-known-hosts"),    
        SSHStrict("ssh-strict"),    
        PersistCredentials("persist-credentials"),    
        Path("path"),    
        Clean("clean"),    
        FetchDepth("fetch-depth"),    
        LFS("lfs"),    
        Submodules("submodules"),    
    }

    companion object {
        const val Uses: String = "actions/checkout@v2"
        val DefaultStepID = Step.StepID("actions-checkout")
    }

    override fun toStep(id: Step.StepID, name: String, uses: String, predicate: String?, env: Environment?): Step {
        return Step.Uses(name, uses, id,
            // preserve parameter order
            linkedMapOf(
                *listOfNotNull(
                    repository?.let { param(InputParameter.Repository, it) },
                    ref?.let { param(InputParameter.Ref, it) },
                    token?.let { param(InputParameter.Token, it) },
                    sshKey?.let { param(InputParameter.SSHKey, it) },
                    sshKnownHosts?.let { param(InputParameter.SSHKnownHosts, it) },
                    sshStrict?.let { param(InputParameter.SSHStrict, it) },
                    persistCredentials?.let { param(InputParameter.PersistCredentials, it) },
                    path?.let { param(InputParameter.Path, it) },
                    clean?.let { param(InputParameter.Clean, it) },
                    fetchDepth?.let { param(InputParameter.FetchDepth, it) },
                    lfs?.let { param(InputParameter.LFS, it) },
                    submodules?.let { param(InputParameter.Submodules, it) },
                ).toTypedArray()
            ),
            predicate,
            env,
        )
    }

    fun toStep(): Step = toStep(DefaultStepID, "Git Checkout", Uses, null, null)
}