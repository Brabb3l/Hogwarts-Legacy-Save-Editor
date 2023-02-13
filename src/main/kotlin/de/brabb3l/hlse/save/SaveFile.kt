package de.brabb3l.hlse.save

import de.brabb3l.hlse.io.BinaryInputStream
import de.brabb3l.hlse.io.BinaryOutputStream
import de.brabb3l.hlse.io.readLengthPrefixedString
import de.brabb3l.hlse.io.writeLengthPrefixedString
import de.brabb3l.hlse.property.structs.DynamicStruct
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SaveFile(
    val saveVersion: Int,
    val u0: Int,
    val engineVersion: UnrealVersion,
    val u1: Int,
    val teamCityData: TeamCityData,
    val gameData: GameData,
) {

    fun write(writer: BinaryOutputStream) {
        writer.writeString("GVAS")

        writer.writeInt(saveVersion)
        writer.writeInt(u0)
        engineVersion.write(writer)
        writer.writeInt(u1)
        teamCityData.write(writer)
        gameData.write(writer)

        writer.writeInt(0) // unknown
    }

    companion object {

        fun parse(reader: BinaryInputStream) : SaveFile {
            reader.readInt() // magic number

            val saveVersion = reader.readInt()
            val u0 = reader.readInt()
            val engineVersion = UnrealVersion.parse(reader)
            val u1 = reader.readInt()
            val teamCityData = TeamCityData.parse(reader)
            val gameData = GameData.parse(reader)

            reader.readInt() // unknown

            return SaveFile(saveVersion, u0, engineVersion, u1, teamCityData, gameData)
        }

    }
}

@Serializable
data class UnrealVersion(
    val major: Short,
    val minor: Short,
    val patch: Short,
) {

    fun write(writer: BinaryOutputStream) {
        writer.writeShort(major)
        writer.writeShort(minor)
        writer.writeShort(patch)
    }

    companion object {

        fun parse(reader: BinaryInputStream) : UnrealVersion {
            val major = reader.readShort()
            val minor = reader.readShort()
            val patch = reader.readShort()

            return UnrealVersion(major, minor, patch)
        }

    }

}

@Serializable
data class TeamCityData(
    val name: String,
    val u0: Int,
    val data: MutableList<Unk1>,
) {

    fun write(writer: BinaryOutputStream) {
        writer.writeLengthPrefixedString(name)
        writer.writeInt(u0)
        writer.writeInt(data.size)
        data.forEach { it.write(writer) }
    }

    companion object {

        fun parse(reader: BinaryInputStream) : TeamCityData {
            val name = reader.readLengthPrefixedString()
            val u0 = reader.readInt()
            val count = reader.readInt()
            val u1 = mutableListOf<Unk1>()

            for (i in 0 until count)
                u1.add(Unk1.parse(reader))

            return TeamCityData(name, u0, u1)
        }

    }
}

@Serializable
data class Unk1(
    val guid: ByteArray,
    val u0: Int,
) {

    fun write(writer: BinaryOutputStream) {
        writer.writeBytes(guid)
        writer.writeInt(u0)
    }

    companion object {

        fun parse(reader: BinaryInputStream) : Unk1 {
            val guid = reader.readBytes(16)
            val u0 = reader.readInt()

            return Unk1(guid, u0)
        }

    }
}

@Serializable
data class GameData(
    var path: String,
    @Transient var data: DynamicStruct? = null,
) {

    fun write(writer: BinaryOutputStream) {
        writer.writeLengthPrefixedString(path)
        data?.write(writer) ?: throw IllegalStateException("Data is null")
    }

    companion object {

        fun parse(reader: BinaryInputStream) : GameData {
            val path = reader.readLengthPrefixedString()
            val data = DynamicStruct(path).apply { parse(reader) }

            return GameData(path, data)
        }

    }
}