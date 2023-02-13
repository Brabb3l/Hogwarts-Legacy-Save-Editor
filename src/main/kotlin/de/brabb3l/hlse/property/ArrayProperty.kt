package de.brabb3l.hlse.property

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.io.readLengthPrefixedString
import de.brabb3l.hlse.io.writeLengthPrefixedString
import de.brabb3l.hlse.property.array.ByteArrayProperty
import de.brabb3l.hlse.property.array.IntArrayProperty
import de.brabb3l.hlse.property.array.StringArrayProperty
import kotlinx.serialization.Serializable

@Serializable
abstract class ArrayProperty : SerializedProperty() {

    abstract val elementType: String

    override val type: String
        get() = "ArrayProperty"

    override val size: Int
        get() = super.size + (4 + elementType.length + 1) + 1 + contentSize

    override fun write(writer: BinaryOutputStream) {
        super.write(writer)

        writer.writeLengthPrefixedString(elementType)
        writer.writeByte(0)

        writeContent(writer)
    }

    companion object {

        fun parse(name: String, index: Int, reader: BinaryInputStream) : ArrayProperty {
            val elementType = reader.readLengthPrefixedString()

            reader.readByte() // unknown

            return when (elementType) {
                "IntProperty" -> {
                    val count = reader.readInt()
                    val array = IntArray(count)

                    for (i in 0 until count)
                        array[i] = reader.readInt()

                    IntArrayProperty(name, index, elementType, array)
                }
                "StrProperty" -> {
                    val count = reader.readInt()
                    val array = mutableListOf<String>()

                    for (i in 0 until count)
                        array.add(reader.readLengthPrefixedString())

                    StringArrayProperty(name, index, elementType, array)
                }
                "NameProperty" -> {
                    val count = reader.readInt()
                    val array = mutableListOf<String>()

                    for (i in 0 until count)
                        array.add(reader.readLengthPrefixedString())

                    StringArrayProperty(name, index, elementType, array)
                }
                "ByteProperty" -> {
                    val count = reader.readInt()
                    val array = reader.readBytes(count)

                    ByteArrayProperty(name, index, elementType, array)
                }
                else -> throw Exception("Unknown array element type: $elementType")
            }
        }

    }

}