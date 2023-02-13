package de.brabb3l.hlse.property

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.io.readLengthPrefixedString
import de.brabb3l.hlse.io.writeLengthPrefixedString
import kotlinx.serialization.Serializable

@Serializable
abstract class SerializedProperty {

    abstract var name: String
    abstract var index: Int

    abstract val type: String
    abstract val contentSize: Int

    open val size: Int get() = (4 + name.length + 1) + (4 + type.length + 1) + 4 + 4

    open fun write(writer: BinaryOutputStream) {
        writer.writeLengthPrefixedString(name)
        writer.writeLengthPrefixedString(type)
        writer.writeInt(contentSize)
        writer.writeInt(index)
    }

    abstract fun writeContent(writer: BinaryOutputStream)

    companion object {

        fun parse(reader: BinaryInputStream) : SerializedProperty? {
            val name = reader.readLengthPrefixedString()

            if (name == "None")
                return null

            val type = reader.readLengthPrefixedString()
            val size = reader.readInt()
            val index = reader.readInt()

            val property = when (type) {
                "ArrayProperty"     -> ArrayProperty.parse(name, index, reader)
                "IntProperty"       -> IntProperty.parse(name, index, reader)
                "ByteProperty"      -> ByteProperty.parse(name, index, reader)
                "EnumProperty"      -> EnumProperty.parse(name, index, reader)
                "BoolProperty"      -> BoolProperty.parse(name, index, reader)
                "StrProperty"       -> StrProperty.parse(name, index, reader)
                "NameProperty"      -> NameProperty.parse(name, index, reader)
                "StructProperty"    -> StructProperty.parse(name, index, reader)
                "MapProperty"       -> MapProperty.parse(name, index, reader)
                "Int64Property"     -> Int64Property.parse(name, index, reader)
                else                -> throw Exception("Unknown property type: $type")
            }

            if (size != property.contentSize)
                throw Exception("Property size mismatch: $size != ${property.contentSize} (${property.type})")

            return property
        }

    }

}