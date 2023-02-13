package de.brabb3l.hlse.property.array

import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.property.ArrayProperty
import de.brabb3l.hlse.property.SerializedProperty
import kotlinx.serialization.Serializable

@Serializable
data class ExplicitArrayProperty(
    override var name: String,
    override var index: Int,
    override val elementType: String,
    val elements: MutableList<SerializedProperty>
) : ArrayProperty() {

    override val contentSize: Int
        get() = when (elementType) {
            "StrProperty"    -> 4 + elements.sumOf { it.contentSize }
            "NameProperty"   -> 4 + elements.sumOf { it.contentSize }
            "IntProperty"    -> 4 + elements.size * 4
            "ByteProperty"   -> 4 + elements.size
            else             -> throw Exception("Unknown array element type: $elementType")
        }

    override fun writeContent(writer: BinaryOutputStream) {
        writer.writeInt(elements.size)

        for (element in elements) {
            element.writeContent(writer)
        }
    }

}