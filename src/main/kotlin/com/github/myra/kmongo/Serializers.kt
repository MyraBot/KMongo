package com.github.myra.kmongo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Color

internal object ColorHexSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("color_hex", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color) {
        val hex = String.format("#%06x", value.rgb and 0xFFFFFF)
        encoder.encodeString(hex)
    }

    override fun deserialize(decoder: Decoder): Color {
        return Color.decode(decoder.decodeString())
    }
}