package de.brabb3l.hlse.save

import de.brabb3l.hlse.property.*
import de.brabb3l.hlse.property.array.ByteArrayProperty
import de.brabb3l.hlse.property.array.StringArrayProperty
import de.brabb3l.hlse.property.structs.DateTimeStruct
import de.brabb3l.hlse.property.structs.DynamicStruct
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PersistentGameData(

    var version: Int,
    var changeList: Int,
    var sessionTime: Long,

    var exclusiveVersion: Int,
    var exclusiveChangeList: Int,
    var exclusiveSessionTime: Long,

    @Transient var rawDatabaseImage: ByteArray = ByteArray(0),
    @Transient var rawExclusiveDatabaseImage: ByteArray = ByteArray(0),
    @Transient var rawMinimapImage: ByteArray = ByteArray(0),

    var characterSaveGameInfo: CharacterSaveGameInfo,

    var hasCompletedIntro: Boolean,
    var entitlements: MutableList<String>,

    var directoryEntry: DirectoryEntry,

    ) {

    fun toStruct() : DynamicStruct {
        val struct = DynamicStruct("")

        struct.addInt("Version", version)
        struct.addInt("ChangeList", changeList)
        struct.addLong("SessionTime", sessionTime)

        struct.addLong("ExclusiveSessionTime", exclusiveSessionTime)
        struct.addInt("ExclusiveVersion", exclusiveVersion)
        struct.addInt("ExclusiveChangeList", exclusiveChangeList)

        struct.addByteArray("RawDatabaseImage", rawDatabaseImage)
        struct.addByteArray("RawExclusiveImage", rawExclusiveDatabaseImage)
        struct.addByteArray("RawMiniMapImage", rawMinimapImage)

        struct.addStruct("CharacterSaveGameInfo", characterSaveGameInfo.toStruct())

        struct.addBool("bCompletedIntro", hasCompletedIntro)
        struct.addStringArray("Entitlements", entitlements)

        struct.addStruct("DirectoryEntry", directoryEntry.toStruct())

        return struct
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersistentGameData

        if (version != other.version) return false
        if (changeList != other.changeList) return false
        if (sessionTime != other.sessionTime) return false
        if (exclusiveVersion != other.exclusiveVersion) return false
        if (exclusiveChangeList != other.exclusiveChangeList) return false
        if (exclusiveSessionTime != other.exclusiveSessionTime) return false
        if (!rawDatabaseImage.contentEquals(other.rawDatabaseImage)) return false
        if (!rawExclusiveDatabaseImage.contentEquals(other.rawExclusiveDatabaseImage)) return false
        if (!rawMinimapImage.contentEquals(other.rawMinimapImage)) return false
        if (characterSaveGameInfo != other.characterSaveGameInfo) return false
        if (hasCompletedIntro != other.hasCompletedIntro) return false
        if (entitlements != other.entitlements) return false
        if (directoryEntry != other.directoryEntry) return false

        return true
    }

    override fun hashCode(): Int {
        var result = version

        result = 31 * result + changeList
        result = 31 * result + sessionTime.hashCode()
        result = 31 * result + exclusiveVersion
        result = 31 * result + exclusiveChangeList
        result = 31 * result + exclusiveSessionTime.hashCode()
        result = 31 * result + rawDatabaseImage.contentHashCode()
        result = 31 * result + rawExclusiveDatabaseImage.contentHashCode()
        result = 31 * result + rawMinimapImage.contentHashCode()
        result = 31 * result + characterSaveGameInfo.hashCode()
        result = 31 * result + hasCompletedIntro.hashCode()
        result = 31 * result + entitlements.hashCode()
        result = 31 * result + directoryEntry.hashCode()

        return result
    }

    companion object {

        fun of(struct: DynamicStruct) : PersistentGameData {
            val version = struct.get<IntProperty>("Version").value
            val changeList = struct.get<IntProperty>("ChangeList").value
            val sessionTime = struct.get<Int64Property>("SessionTime").value

            val exclusiveVersion = struct.get<IntProperty>("ExclusiveVersion").value
            val exclusiveChangeList = struct.get<IntProperty>("ExclusiveChangeList").value
            val exclusiveSessionTime = struct.get<Int64Property>("ExclusiveSessionTime").value

            val rawDatabaseImage = struct.get<ByteArrayProperty>("RawDatabaseImage").elements
            val rawExclusiveDatabaseImage = struct.get<ByteArrayProperty>("RawExclusiveImage").elements
            val rawMinimapImage = struct.get<ByteArrayProperty>("RawMiniMapImage").elements

            val characterSaveGameInfo = CharacterSaveGameInfo.of(struct.getDynamicStruct("CharacterSaveGameInfo"))

            val hasCompletedIntro = struct.get<BoolProperty>("bCompletedIntro").value
            val entitlements = struct.get<StringArrayProperty>("Entitlements").elements

            val directoryEntry = DirectoryEntry.of(struct.getDynamicStruct("DirectoryEntry"))

            return PersistentGameData(
                version,
                changeList,
                sessionTime,
                exclusiveVersion,
                exclusiveChangeList,
                exclusiveSessionTime,
                rawDatabaseImage,
                rawExclusiveDatabaseImage,
                rawMinimapImage,
                characterSaveGameInfo,
                hasCompletedIntro,
                entitlements,
                directoryEntry
            )
        }

    }
}

@Serializable
data class CharacterSaveGameInfo(

    var id: Int,
    var uid: String,
    var name: String,

    var pronoun: String,
    var voice: String,
    var house: String,
    var gender: String,

    var level: Int,
    var difficulty: Int,

    var presetData: MutableList<String>,
    var clothesData: MutableList<String>,

    var gearTags: String,
    var isUsed: Boolean,

    var frontendPlayerState: FrontendPlayerState,

    ) {

    fun toStruct() : DynamicStruct {
        val struct = DynamicStruct("CharacterSaveGameInfo")

        struct.addInt("CharacterID", id)
        struct.addString("CharacterName", name)

        struct.addString("CharacterPronoun", pronoun)
        struct.addString("CharacterVoice", voice)
        struct.addString("CharacterHouse", house)
        struct.addString("CharacterGender", gender)

        struct.addInt("CharacterLevel", level)
        struct.addByte("CharacterGameDifficulty", difficulty.toByte())

        struct.addNameArray("CharacterPresetData", presetData)
        struct.addNameArray("CharacterClothesData", clothesData)

        struct.addString("GearTags", gearTags)
        struct.addBool("bIsUsed", isUsed)
        struct.addString("CharacterUID", uid)

        struct.addStruct("FrontendPlayerState", frontendPlayerState.toStruct())

        struct.addEnum("CurrentFormat", "ECharacterNameFormat", "WIDE")
        struct.addByteArray("CharacterNameBytes", name.toByteArray(Charsets.UTF_8).let { it.copyOf(it.size + 1) })

        return struct
    }

    companion object {
        fun of(struct: DynamicStruct) : CharacterSaveGameInfo {
            val id = struct.get<IntProperty>("CharacterID").value
            val name = struct.get<StrProperty>("CharacterName").value
            val uid = struct.get<StrProperty>("CharacterUID").value

            val pronoun = struct.get<StrProperty>("CharacterPronoun").value
            val voice = struct.get<StrProperty>("CharacterVoice").value
            val house = struct.get<StrProperty>("CharacterHouse").value
            val gender = struct.get<StrProperty>("CharacterGender").value

            val level = struct.get<IntProperty>("CharacterLevel").value
            val difficulty = struct.get<ByteProperty>("CharacterGameDifficulty").value

            val presetData = struct.get<StringArrayProperty>("CharacterPresetData").elements
            val clothesData = struct.get<StringArrayProperty>("CharacterClothesData").elements

            val gearTags = struct.get<StrProperty>("GearTags").value
            val isUsed = struct.get<BoolProperty>("bIsUsed").value

            val frontendPlayerState = FrontendPlayerState.of(struct.getDynamicStruct("FrontendPlayerState"))

            return CharacterSaveGameInfo(
                id,
                uid,
                name,
                pronoun,
                voice,
                house,
                gender,
                level,
                difficulty.toInt(),
                presetData,
                clothesData,
                gearTags,
                isUsed,
                frontendPlayerState
            )
        }
    }
}

@Serializable
data class FrontendPlayerState(
    var values: MutableMap<String, String>
) {

    fun toStruct() : DynamicStruct {
        val struct = DynamicStruct("FrontendPlayerState")

        val map = mutableMapOf<SerializedProperty, SerializedProperty>()

        values.forEach { (key, value) ->
            map[StrProperty("Element", 0, key)] = StrProperty("Element", 0, value)
        }

        struct.add(MapProperty("Values", 0, "StrProperty", "StrProperty", map))

        return struct
    }

    companion object {

        fun of(struct: DynamicStruct) : FrontendPlayerState {
            val values = mutableMapOf<String, String>()

            struct.get<MapProperty>("Values").elements.forEach { (key, value) ->
                values[(key as StrProperty).value] = (value as StrProperty).value
            }

            return FrontendPlayerState(values)
        }

    }

}

@Serializable
data class DirectoryEntry(
    var fileNameSlot: String,
    var saveType: String,
    var saveIsUsed: Boolean,

    var gameTime: Long,
    var saveTime: Long,

    var currentMap: String,
    var currentAction: String,

    var majorVersion: Int,
    var minorVersion: Int,
) {

    fun toStruct() : DynamicStruct {
        val struct = DynamicStruct("SaveDirectoryEntry")

        struct.addString("FilenameSlot", fileNameSlot)
        struct.addEnum("SaveType", "ESaveType", saveType)
        struct.addBool("bIsUsed", saveIsUsed)

        struct.addStruct("GameTime", DateTimeStruct().also { it.value = gameTime })
        struct.addStruct("SaveTime", DateTimeStruct().also { it.value = saveTime })

        struct.addString("CurrentMap", currentMap)
        struct.addString("CurrentAction", currentAction)

        struct.addInt("MajorVersion", majorVersion)
        struct.addInt("MinorVersion", minorVersion)

        return struct
    }

    companion object {
        fun of(struct: DynamicStruct) : DirectoryEntry {
            val fileNameSlot = struct.get<StrProperty>("FilenameSlot").value
            val saveType = struct.get<EnumProperty>("SaveType").value.split("::").drop(1).joinToString("::")
            val saveIsUsed = struct.get<BoolProperty>("bIsUsed").value

            val gameTime = (struct.get<StructProperty>("GameTime").data as DateTimeStruct).value
            val saveTime = (struct.get<StructProperty>("SaveTime").data as DateTimeStruct).value

            val currentMap = struct.get<StrProperty>("CurrentMap").value
            val currentAction = struct.get<StrProperty>("CurrentAction").value

            val majorVersion = struct.get<IntProperty>("MajorVersion").value
            val minorVersion = struct.get<IntProperty>("MinorVersion").value

            return DirectoryEntry(
                fileNameSlot,
                saveType,
                saveIsUsed,
                gameTime,
                saveTime,
                currentMap,
                currentAction,
                majorVersion,
                minorVersion
            )
        }
    }
}