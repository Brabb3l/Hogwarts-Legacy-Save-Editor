package de.brabb3l.hlse.property.structs

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.io.writeLengthPrefixedString
import de.brabb3l.hlse.property.*
import de.brabb3l.hlse.property.array.ByteArrayProperty
import de.brabb3l.hlse.property.array.StringArrayProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("dynamic")
data class DynamicStruct(
    @SerialName("structType") override val type: String,
    val properties: MutableList<SerializedProperty> = mutableListOf()
) : IStructPropertyData() {

    override val size: Int
        get() = properties.sumOf { it.size } + 9

    override fun parse(reader: BinaryInputStream) {
        var property = SerializedProperty.parse(reader)

        while (property != null) {
            properties.add(property)

            property = SerializedProperty.parse(reader)
        }
    }

    override fun write(writer: BinaryOutputStream) {
        properties.forEach { it.write(writer) }

        writer.writeLengthPrefixedString("None")
    }

    inline fun <reified T> get(name: String): T {
        return properties.first { it.name == name } as T
    }

    inline fun <reified T> getOrNull(name: String): T? {
        return properties.firstOrNull { it.name == name } as T?
    }

    fun getDynamicStruct(name: String): DynamicStruct {
        return (properties.first { it.name == name } as StructProperty).data as DynamicStruct
    }

    fun getDynamicStructOrNull(name: String): DynamicStruct? {
        return (properties.firstOrNull { it.name == name } as StructProperty).data as DynamicStruct?
    }

    fun add(property: SerializedProperty) {
        properties.add(property)
    }

    fun addString(name: String, value: String) {
        add(StrProperty(name, 0, value))
    }

    fun addEnum(name: String, type: String, value: String) {
        add(EnumProperty(name, 0, type, "$type::$value"))
    }

    fun addEnum(name: String, value: Int) {
        add(EnumProperty(name, 0, "None", value.toString()))
    }

    fun addByte(name: String, value: Byte) {
        add(ByteProperty(name, 0, "None", value.toString()))
    }

    fun addInt(name: String, value: Int) {
        add(IntProperty(name, 0, value))
    }

    fun addBool(name: String, value: Boolean) {
        add(BoolProperty(name, 0, value))
    }

    fun addLong(name: String, value: Long) {
        add(Int64Property(name, 0, value))
    }

    fun addByteArray(name: String, value: ByteArray) {
        add(ByteArrayProperty(name, 0, "ByteProperty", value))
    }

    fun addStringArray(name: String, value: MutableList<String>) {
        add(StringArrayProperty(name, 0, "StrProperty", value))
    }

    fun addNameArray(name: String, value: MutableList<String>) {
        add(StringArrayProperty(name, 0, "NameProperty", value))
    }

    fun addStruct(name: String, data: IStructPropertyData) {
        add(StructProperty(name, 0, data.type, ByteArray(16), data))
    }

}
