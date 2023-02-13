package de.brabb3l.hlse.property

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.io.readLengthPrefixedString
import de.brabb3l.hlse.io.writeLengthPrefixedString
import de.brabb3l.hlse.property.structs.DateTimeStruct
import de.brabb3l.hlse.property.structs.DynamicStruct
import de.brabb3l.hlse.property.structs.IStructPropertyData
import kotlinx.serialization.Serializable

@Serializable
data class StructProperty(
    override var name: String,
    override var index: Int,
    val typeName: String,
    val guid: ByteArray,
    val data: IStructPropertyData
) : SerializedProperty() {

    override val type: String
        get() = "StructProperty"

    override val contentSize: Int
        get() = data.size

    override val size: Int
        get() = super.size + (4 + typeName.length + 1) + guid.size + 1 + contentSize

    override fun write(writer: BinaryOutputStream) {
        super.write(writer)

        writer.writeLengthPrefixedString(typeName)
        writer.writeBytes(guid)
        writer.writeByte(0) // Unknown

        writeContent(writer)
    }

    override fun writeContent(writer: BinaryOutputStream) {
        data.write(writer)
    }

    companion object {

        fun parse(name: String, index: Int, reader: BinaryInputStream) : StructProperty {
            val typeName = reader.readLengthPrefixedString()
            val guid = reader.readBytes(16)

            reader.readByte() // Unknown

            val data = when (typeName) {
                "DateTime" -> DateTimeStruct()
                else       -> DynamicStruct(typeName)
            }.apply { parse(reader) }

            return StructProperty(name, index, typeName, guid, data)
        }

    }

}
