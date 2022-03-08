package io.github.nefilim.githubactions

import com.charleskorn.kaml.MultiLineStringStyle
import com.charleskorn.kaml.SingleLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.github.nefilim.githubactions.actions.GitHubActionParameter
import io.github.nefilim.githubactions.domain.Workflow.Job.Step
import io.github.nefilim.githubactions.domain.StepID
import io.github.nefilim.githubactions.domain.Workflow.Triggers.Trigger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object InputSerializer: KSerializer<Trigger.WorkflowDispatch.Input> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("value", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Trigger.WorkflowDispatch.Input) {
        when (value) {
            is Trigger.WorkflowDispatch.Input.Boolean -> encoder.encodeSerializableValue(Trigger.WorkflowDispatch.Input.Boolean.serializer(), value)
            is Trigger.WorkflowDispatch.Input.Choice -> encoder.encodeSerializableValue(Trigger.WorkflowDispatch.Input.Choice.serializer(), value)
            is Trigger.WorkflowDispatch.Input.Environment -> encoder.encodeSerializableValue(Trigger.WorkflowDispatch.Input.Environment.serializer(), value)
            is Trigger.WorkflowDispatch.Input.String -> encoder.encodeSerializableValue(Trigger.WorkflowDispatch.Input.String.serializer(), value)
        }
    }

    override fun deserialize(decoder: Decoder): Trigger.WorkflowDispatch.Input {
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