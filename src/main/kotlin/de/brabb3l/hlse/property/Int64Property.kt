package de.brabb3l.hlse.property

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import kotlinx.serialization.Serializable

@Serializable
data class Int64Property(
    override var name: String,
    override var index: Int,
    val value: Long
) : SerializedProperty() {

    override val type: String
        get() = "Int64Property"

    override val contentSize: Int
        get() = 8

    override val size: Int
        get() = super.size + 1 + contentSize

    override fun write(writer: BinaryOutputStream) {
        super.write(writer)

        writer.writeByte(0) // Unknown
        writeContent(writer)
    }

    override fun writeContent(writer: BinaryOutputStream) {
        writer.writeLong(value)
    }

    companion object {

        fun parse(name: String, index: Int, reader: BinaryInputStream) : Int64Property {
            reader.readByte() // Unknown

            val value = reader.readLong()

            return Int64Property(name, index, value)
        }

    }

}
