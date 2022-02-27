package io.github.nefilim.githubactions.dsl

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object InputSerializer: KSerializer<Trigger.WorkflowDispatch.Companion.Input> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("value", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Trigger.WorkflowDispatch.Companion.Input) {
        when (value) {
            is Trigger.WorkflowDispatch.Companion.Input.Boolean -> encoder.encodeSerializableValue(Trigger.WorkflowDispatch.Companion.Input.Boolean.serializer(), value)
            is Trigger.WorkflowDispatch.Companion.Input.Choice -> encoder.encodeSerializableValue(Trigger.WorkflowDispatch.Companion.Input.Choice.serializer(), value)
            is Trigger.WorkflowDispatch.Companion.Input.Environment -> encoder.encodeSerializableValue(Trigger.WorkflowDispatch.Companion.Input.Environment.serializer(), value)
            is Trigger.WorkflowDispatch.Companion.Input.String -> encoder.encodeSerializableValue(Trigger.WorkflowDispatch.Companion.Input.String.serializer(), value)
        }
    }

    override fun deserialize(decoder: Decoder): Trigger.WorkflowDispatch.Companion.Input {
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