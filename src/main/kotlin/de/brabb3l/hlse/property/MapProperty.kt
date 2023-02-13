package de.brabb3l.hlse.property

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.io.readLengthPrefixedString
import de.brabb3l.hlse.io.writeLengthPrefixedString
import kotlinx.serialization.Serializable

@Serializable
data class MapProperty(
    override var name: String,
    override var index: Int,
    val keyType: String,
    val valueType: String,
    val elements: MutableMap<SerializedProperty, SerializedProperty>
) : SerializedProperty() {

    override val type: String
        get() = "MapProperty"

    override val contentSize: Int
        get() = elements.entries.sumOf { (k, v) -> k.contentSize + v.contentSize } + 4 + 4

    override val size: Int
        get() = super.size + (4 + keyType.length + 1) + (4 + valueType.length + 1) + 1 + contentSize

    override fun write(writer: BinaryOutputStream) {
        super.write(writer)

        writer.writeLengthPrefixedString(keyType)
        writer.writeLengthPrefixedString(valueType)

        writer.writeByte(0) // Unknown

        writeContent(writer)
    }

    override fun writeContent(writer: BinaryOutputStream) {
        writer.writeInt(0) // Unknown
        writer.writeInt(elements.size)

        for ((key, value) in elements) {
            key.writeContent(writer)
            value.writeContent(writer)
        }
    }

    companion object {

        fun parse(name: String, index: Int, reader: BinaryInputStream) : MapProperty {
            val property = MapProperty(name, index, reader.readLengthPrefixedString(), reader.readLengthPrefixedString(), mutableMapOf())

            reader.readByte() // Unknown

            val u1 = reader.readInt()
            val count = reader.readInt()

            for (i in 0 until count) {
                val key = parse(property.keyType, reader)
                val value = parse(property.valueType, reader)

                property.elements[key] = value
            }

            return property
        }

        private fun parse(type: String, reader: BinaryInputStream) : SerializedProperty {
            return when (type) {
                "StrProperty" -> StrProperty("Element", 0, reader.readLengthPrefixedString())
                else -> throw Exception("Unknown property type: $type")
            }
        }

    }

}