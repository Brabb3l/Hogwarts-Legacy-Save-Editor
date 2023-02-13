package de.brabb3l.hlse.property

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.io.readLengthPrefixedString
import de.brabb3l.hlse.io.writeLengthPrefixedString
import kotlinx.serialization.Serializable

@Serializable
data class NameProperty(
    override var name: String,
    override var index: Int,
    val value: String
) : SerializedProperty() {

    override val type: String
        get() = "NameProperty"

    override val contentSize: Int
        get() = 4 + value.length + 1

    override val size: Int
        get() = super.size + 1 + contentSize

    override fun write(writer: BinaryOutputStream) {
        super.write(writer)

        writer.writeByte(0) // Unknown
        writeContent(writer)
    }

    override fun writeContent(writer: BinaryOutputStream) {
        writer.writeLengthPrefixedString(value)
    }

    companion object {

        fun parse(name: String, index: Int, reader: BinaryInputStream) : NameProperty {
            reader.readByte() // Unknown

            val value = reader.readLengthPrefixedString()

            return NameProperty(name, index, value)
        }

    }

}
