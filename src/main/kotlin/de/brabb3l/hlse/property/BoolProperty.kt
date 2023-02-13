package de.brabb3l.hlse.property

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import kotlinx.serialization.Serializable

@Serializable
data class BoolProperty(
    override var name: String,
    override var index: Int,
    val value: Boolean
) : SerializedProperty() {

    override val type: String
        get() = "BoolProperty"

    override val contentSize: Int
        get() = 0

    override val size: Int
        get() = super.size + 1 + 1

    override fun write(writer: BinaryOutputStream) {
        super.write(writer)

        writeContent(writer)
        writer.writeByte(0) // Unknown
    }

    override fun writeContent(writer: BinaryOutputStream) {
        writer.writeByte(if (value) 1 else 0)
    }

    companion object {

        fun parse(name: String, index: Int, reader: BinaryInputStream) : BoolProperty {
            val value = reader.readByte() > 0

            reader.readByte() // Unknown

            return BoolProperty(name, index, value)
        }

    }

}
