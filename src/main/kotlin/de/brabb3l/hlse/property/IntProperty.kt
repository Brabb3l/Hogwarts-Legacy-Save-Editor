package de.brabb3l.hlse.property

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import kotlinx.serialization.Serializable

@Serializable
data class IntProperty(
    override var name: String,
    override var index: Int,
    val value: Int
) : SerializedProperty() {

    override val type: String
        get() = "IntProperty"

    override val contentSize: Int
        get() = 4

    override val size: Int
        get() = super.size + 1 + contentSize

    override fun write(writer: BinaryOutputStream) {
        super.write(writer)

        writer.writeByte(0) // Unknown
        writeContent(writer)
    }

    override fun writeContent(writer: BinaryOutputStream) {
        writer.writeInt(value)
    }

    companion object {

        fun parse(name: String, index: Int, reader: BinaryInputStream) : IntProperty {
            reader.readByte() // Unknown

            val value = reader.readInt()

            return IntProperty(name, index, value)
        }

    }

}
