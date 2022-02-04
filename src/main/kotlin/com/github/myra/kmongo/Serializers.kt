package com.github.myra.kmongo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Color

@Serializer(forClass = Color::class)
internal object ColorHexSerializer : KSerializer<Color?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("color_hex", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            val hex = String.format("#%06x", value.rgb and 0xFFFFFF)
            encoder.encodeString(hex)
        }
    }

    override fun deserialize(decoder: Decoder): Color? {
        //val hex = decoder.decodeString()
        //return Color.decode(hex)r
        return Color.BLUE
    }
}