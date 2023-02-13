package de.brabb3l.hlse.property.structs

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.properties.Delegates

@Serializable
@SerialName("DateTime")
class DateTimeStruct : IStructPropertyData() {

    override val type: String
        get() = "DateTime"

    var value by Delegates.notNull<Long>()

    override val size: Int
        get() = 8

    override fun parse(reader: BinaryInputStream) {
        value = reader.readLong()
    }

    override fun write(writer: BinaryOutputStream) {
        writer.writeLong(value)
    }

}