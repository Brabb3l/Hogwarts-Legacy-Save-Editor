package de.brabb3l.hlse.property

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.io.readLengthPrefixedString
import de.brabb3l.hlse.io.writeLengthPrefixedString
import kotlinx.serialization.Serializable

@Serializable
data class EnumProperty(
    override var name: String,
    override var index: Int,
    val valueType: String,
    val value: String
) : SerializedProperty() {

    override val type: String
        get() = "EnumProperty"

    override val contentSize: Int
        get() = if (valueType == "None") 1 else 4 + value.length + 1

    override val size: Int
        get() = super.size + (4 + valueType.length + 1) + 1 + contentSize

    override fun write(writer: BinaryOutputStream) {
        super.write(writer)

        writer.writeLengthPrefixedString(valueType)
        writer.writeByte(0) // Unknown

        if (valueType == "None")
            writer.writeByte(value.toByte())
        else
            writer.writeLengthPrefixedString(value)
    }

    override fun writeContent(writer: BinaryOutputStream) {
        writer.writeLengthPrefixedString(value)
    }

    companion object {

        fun parse(name: String, index: Int, reader: BinaryInputStream) : EnumProperty {
            val valueType = reader.readLengthPrefixedString()

            reader.readByte() // Unknown

            val value = if (valueType == "None") reader.readByte().toString() else reader.readLengthPrefixedString()

            return EnumProperty(name, index, valueType, value)
        }

    }

}
