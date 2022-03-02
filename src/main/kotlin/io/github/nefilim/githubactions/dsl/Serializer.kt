package io.github.nefilim.githubactions.dsl

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

object StepIDSerializer: KSerializer<Step.StepID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("stepID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Step.StepID) {
        encoder.encodeString(value.id)
    }

    override fun deserialize(decoder: Decoder): Step.StepID {
        TODO("deserialization of Step.StepID not implemented")
    }
}