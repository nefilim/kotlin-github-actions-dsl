package io.github.nefilim.githubactions

import com.charleskorn.kaml.MultiLineStringStyle
import com.charleskorn.kaml.SingleLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.github.nefilim.githubactions.domain.GitHubActionParameter
import io.github.nefilim.githubactions.domain.WorkflowCommon.Job.Step
import io.github.nefilim.githubactions.domain.StepID
import io.github.nefilim.githubactions.domain.WorkflowCommon
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object InputSerializer: KSerializer<WorkflowCommon.Input> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("value", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: WorkflowCommon.Input) {
        when (value) {
            is WorkflowCommon.Input.Boolean -> encoder.encodeSerializableValue(WorkflowCommon.Input.Boolean.serializer(), value)
            is WorkflowCommon.Input.Choice -> encoder.encodeSerializableValue(WorkflowCommon.Input.Choice.serializer(), value)
            is WorkflowCommon.Input.Environment -> encoder.encodeSerializableValue(WorkflowCommon.Input.Environment.serializer(), value)
            is WorkflowCommon.Input.String -> encoder.encodeSerializableValue(WorkflowCommon.Input.String.serializer(), value)
        }
    }

    override fun deserialize(decoder: Decoder): WorkflowCommon.Input {
        TODO("deserialization of WorkflowDispatch.Input not implemented")
    }
}

object StepSerializer: KSerializer<Step> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("step", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Step) {
        when (value) {
            is Step.Uses -> encoder.encodeSerializableValue(Step.Uses.serializer(), value)
            is Step.Runs -> encoder.encodeSerializableValue(Step.Runs.serializer(), value)
        }
    }

    override fun deserialize(decoder: Decoder): Step {
        TODO("deserialization of Step not implemented")
    }
}

object StepIDSerializer: KSerializer<StepID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("stepID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: StepID) {
        encoder.encodeString(value.id)
    }

    override fun deserialize(decoder: Decoder): StepID {
        TODO("deserialization of Step.StepID not implemented")
    }
}

object JobIDSerializer: KSerializer<WorkflowCommon.JobID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("jobID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: WorkflowCommon.JobID) {
        encoder.encodeString(value.id)
    }

    override fun deserialize(decoder: Decoder): WorkflowCommon.JobID {
        TODO("deserialization of WorkflowCommon.JobID not implemented")
    }
}

object GitHubActionParameterSerializer: KSerializer<GitHubActionParameter> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("gitHubActionParameter", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: GitHubActionParameter) {
        encoder.encodeString(value.parameter)
    }

    override fun deserialize(decoder: Decoder): GitHubActionParameter {
        TODO("deserialization of Step.StepID not implemented")
    }
}

val GitHubActionsYAML = Yaml(
    configuration = YamlConfiguration(
        breakScalarsAt = 200,
        multiLineStringStyle = MultiLineStringStyle.Literal,
        singleLineScalarStyle = SingleLineStringStyle.SingleQuoted,
    )
)