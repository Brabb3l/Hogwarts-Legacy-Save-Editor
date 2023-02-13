package de.brabb3l.hlse.property.array

import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.property.ArrayProperty
import kotlinx.serialization.Serializable

@Serializable
data class ByteArrayProperty(
    override var name: String,
    override var index: Int,
    override val elementType: String,
    var elements: ByteArray
) : ArrayProperty() {

    override val contentSize: Int = 4 + elements.size

    override fun writeContent(writer: BinaryOutputStream) {
        writer.writeInt(elements.size)
        writer.writeBytes(elements)
    }

}