package de.brabb3l.hlse.io

fun BinaryInputStream.readLengthPrefixedString() : String {
    val length = readInt() - 1
    val string = readChars(length)

    readByte() // Null terminator

    return string
}

fun BinaryOutputStream.writeLengthPrefixedString(string: String) {
    writeInt(string.length + 1)
    writeString(string)
    writeByte(0x00)
}