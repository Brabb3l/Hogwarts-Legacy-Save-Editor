package de.brabb3l.hlse.property.structs

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import kotlinx.serialization.Serializable

@Serializable
abstract class IStructPropertyData {

    abstract val type: String

    abstract val size: Int

    abstract fun parse(reader: BinaryInputStream)

    abstract fun write(writer: BinaryOutputStream)

}