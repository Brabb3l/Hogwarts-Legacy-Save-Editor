package de.brabb3l.hlse.property.array

import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.io.writeLengthPrefixedString
import de.brabb3l.hlse.property.ArrayProperty
import kotlinx.serialization.Serializable

@Serializable
data class StringArrayProperty(
    override var name: String,
    override var index: Int,
    override val elementType: String,
    var elements: MutableList<String>
) : ArrayProperty() {

    override val contentSize: Int = 4 + elements.sumOf { 4 + it.length + 1 }

    override fun writeContent(writer: BinaryOutputStream) {
        writer.writeInt(elements.size)

        elements.forEach { writer.writeLengthPrefixedString(it) }
    }

}