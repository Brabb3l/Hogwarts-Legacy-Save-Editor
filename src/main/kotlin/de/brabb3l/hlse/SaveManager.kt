package de.brabb3l.hlse

import de.brabb3l.hlse.save.PersistentGameData
import de.brabb3l.hlse.save.SaveFile
import de.brabb3l.hlse.io.asBinaryInputStream
import de.brabb3l.hlse.io.asBinaryOutputStream
import de.brabb3l.hlse.property.*
import de.brabb3l.hlse.property.array.ByteArrayProperty
import de.brabb3l.hlse.property.array.ExplicitArrayProperty
import de.brabb3l.hlse.property.array.IntArrayProperty
import de.brabb3l.hlse.property.array.StringArrayProperty
import de.brabb3l.hlse.property.structs.DateTimeStruct
import de.brabb3l.hlse.property.structs.DynamicStruct
import de.brabb3l.hlse.property.structs.IStructPropertyData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.io.File
import java.sql.DriverManager

object SaveManager {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        serializersModule = SerializersModule {
            polymorphic(IStructPropertyData::class) {
                subclass(DynamicStruct::class)
                subclass(DateTimeStruct::class)
            }

            polymorphic(SerializedProperty::class) {
                subclass(BoolProperty::class)
                subclass(ByteProperty::class)
                subclass(EnumProperty::class)
                subclass(Int64Property::class)
                subclass(IntProperty::class)
                subclass(MapProperty::class)
                subclass(NameProperty::class)
                subclass(StrProperty::class)
                subclass(StructProperty::class)

                subclass(ExplicitArrayProperty::class)
                subclass(ByteArrayProperty::class)
                subclass(IntArrayProperty::class)
                subclass(StringArrayProperty::class)
            }

            polymorphic(ArrayProperty::class) {
                subclass(ExplicitArrayProperty::class)
                subclass(ByteArrayProperty::class)
                subclass(IntArrayProperty::class)
                subclass(StringArrayProperty::class)
            }
        }
    }

    fun unpack(saveFile: File, output: File) {
        if (saveFile.isDirectory)
            throw IllegalArgumentException("${saveFile.path} is not a file")

        output.mkdirs()

        val headerFile = File(output, "header.json")
        val dataFile = File(output, "data.json")
        val db0 = File(output, "main.db")
        val db1 = File(output, "exclusive.db")
        val img = File(output, "minimap.bin")

        val sf = saveFile.inputStream().use { SaveFile.parse(it.asBinaryInputStream()) }
        val data = PersistentGameData.of(sf.gameData.data ?: throw IllegalStateException("Data is null"))

        headerFile.writeText(json.encodeToString(sf))
        dataFile.writeText(json.encodeToString(data))
        db0.writeBytes(data.rawDatabaseImage)
        db1.writeBytes(data.rawExclusiveDatabaseImage)
        img.writeBytes(data.rawMinimapImage)

        unpackDatabase(db0)
        unpackDatabase(db1)
    }

    fun pack(unpackedDirectory: File, outputFile: File) {
        if (!unpackedDirectory.isDirectory)
            throw IllegalArgumentException("${unpackedDirectory.path} is not a directory")

        if (outputFile.isDirectory)
            throw IllegalArgumentException("${outputFile.path} is not a file")

        if (!unpackedDirectory.exists())
            throw IllegalArgumentException("${unpackedDirectory.path} does not exist")

        val headerFile = File(unpackedDirectory, "header.json")
        val dataFile = File(unpackedDirectory, "data.json")
        val db0 = File(unpackedDirectory, "main.db")
        val db1 = File(unpackedDirectory, "exclusive.db")
        val img = File(unpackedDirectory, "minimap.bin")

        if (!headerFile.exists()) throw IllegalArgumentException("${headerFile.path} does not exist")
        if (!dataFile.exists()) throw IllegalArgumentException("${dataFile.path} does not exist")
        if (!db0.exists()) throw IllegalArgumentException("${db0.path} does not exist")
        if (!db1.exists()) throw IllegalArgumentException("${db1.path} does not exist")
        if (!img.exists()) throw IllegalArgumentException("${img.path} does not exist")

        val sf = json.decodeFromString<SaveFile>(headerFile.readText())
        val data = json.decodeFromString<PersistentGameData>(dataFile.readText())

        packDatabase(db0)
        packDatabase(db1)

        data.rawDatabaseImage = db0.readBytes()
        data.rawExclusiveDatabaseImage = db1.readBytes()
        data.rawMinimapImage = img.readBytes()

        sf.gameData.data = data.toStruct()

        outputFile.outputStream().use { sf.write(it.asBinaryOutputStream()) }
    }

    private fun unpackDatabase(db0: File) {
        val dbDir = File(db0.parentFile, db0.nameWithoutExtension).apply { mkdirs() }
        val c = DriverManager.getConnection("jdbc:sqlite:${db0.absolutePath}")

        val rs = c.metaData.getTables(null, null, null, null)
        val tables = mutableListOf<String>()

        while (rs.next()) {
            val tableName = rs.getString("TABLE_NAME")

            if (tableName.startsWith("sqlite"))
                continue

            tables += tableName
        }

        tables.stream().parallel().forEach { tableName ->
            c.createStatement().use table@ { stmt ->
                val table = stmt.executeQuery("SELECT * FROM $tableName")
                val data = mutableListOf<Map<String, String?>>()

                while (table.next()) {
                    val row = mutableMapOf<String, String?>()

                    for (i in 1..table.metaData.columnCount) {
                        val name = table.metaData.getColumnName(i)
                        val value = table.getObject(i)

                        row[name] = value?.toString()
                    }

                    data += row
                }

                File(dbDir, "$tableName.json").writeText(json.encodeToString(data))
            }
        }
    }

    private fun packDatabase(db0: File) {
        val dbDir = File(db0.parentFile, db0.nameWithoutExtension).apply { mkdirs() }
        val c = DriverManager.getConnection("jdbc:sqlite:${db0.absolutePath}")

        val rs = c.metaData.getTables(null, null, null, null)
        val tables = mutableListOf<String>()

        while (rs.next()) {
            val tableName = rs.getString("TABLE_NAME")

            if (tableName.startsWith("sqlite"))
                continue

            tables += tableName
        }

        tables.stream().parallel().forEach { tableName ->
            val file = File(dbDir, "$tableName.json")

            if (!file.exists())
                return@forEach

            val data = json.decodeFromString<List<Map<String, String?>>>(file.readText())

            val elements = c.createStatement().use { stmt ->
                val elementsResult = stmt.executeQuery("SELECT * FROM $tableName")
                val elements = mutableListOf<Map<String, String?>>()

                while (elementsResult.next()) {
                    val row = mutableMapOf<String, String?>()

                    for (i in 1..elementsResult.metaData.columnCount) {
                        val name = elementsResult.metaData.getColumnName(i)
                        val value = elementsResult.getObject(i)

                        row[name] = value?.toString()
                    }

                    elements += row
                }

                elements
            }

            val pkResult  = c.metaData.getPrimaryKeys(null, null, tableName)
            val colResult = c.metaData.getColumns(null, null, tableName, null)

            val columnNames = mutableListOf<String>()
            val primaryKeys = mutableListOf<String>()
            val nonPrimaryKeys = mutableListOf<String>()

            while (colResult.next())
                columnNames += colResult.getString("COLUMN_NAME")

            while (pkResult.next())
                primaryKeys += pkResult.getString("COLUMN_NAME")

            nonPrimaryKeys += columnNames.filter { !primaryKeys.contains(it) }

            if (primaryKeys.isEmpty())
                primaryKeys += columnNames

            val placeholder1 = columnNames.joinToString(", ") { "?" }
            val placeholder2 = nonPrimaryKeys.joinToString(", ") { "$it = ?" }
            val placeholder3 = primaryKeys.joinToString(" AND ") { "$it = ?" }

            val insert = c.prepareStatement("INSERT INTO $tableName VALUES ($placeholder1)")
            val delete = c.prepareStatement("DELETE FROM $tableName WHERE $placeholder3")
            val update = if (nonPrimaryKeys.isNotEmpty())
                c.prepareStatement("UPDATE $tableName SET $placeholder2 WHERE $placeholder3")
            else null

            data.forEach { row ->
                // check for existing row with primary keys
                val existing = elements.find { element ->
                    primaryKeys.all { element[it] == row[it] }
                }

                if (existing == null) {
                    // insert
                    insert.clearParameters()

                    for (i in 1..columnNames.size) {
                        val name = columnNames[i - 1]
                        val value = row[name]

                        insert.setObject(i, value)
                    }

                    insert.addBatch()
                } else if (update != null) {
                    // update
                    update.clearParameters()

                    for (i in 1..nonPrimaryKeys.size) {
                        val name = nonPrimaryKeys[i - 1]
                        val value = row[name]

                        update.setObject(i, value)
                    }

                    for (i in 1..primaryKeys.size) {
                        val name = primaryKeys[i - 1]
                        val value = row[name]

                        update.setObject(i + nonPrimaryKeys.size, value)
                    }

                    update.addBatch()
                }
            }

            elements.forEach { element ->
                // check for existing row with primary keys
                val existing = data.find { row ->
                    primaryKeys.all { element[it] == row[it] }
                }

                if (existing == null) {
                    // delete
                    delete.clearParameters()

                    for (i in 1..primaryKeys.size) {
                        val name = primaryKeys[i - 1]
                        val value = element[name]

                        delete.setObject(i, value)
                    }

                    delete.addBatch()
                }
            }

            insert.executeBatch()
            update?.executeBatch()
            delete.executeBatch()

            insert.close()
            update?.close()
            delete.close()
        }
    }

}