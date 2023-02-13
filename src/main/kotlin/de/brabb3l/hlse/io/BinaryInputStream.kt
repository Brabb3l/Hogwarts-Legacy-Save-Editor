package de.brabb3l.hlse.io

import java.io.InputStream
import java.nio.ByteOrder

class BinaryInputStream(private val input: InputStream) {
    
    var byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN
    
    fun readByte(): Byte {
        return input.read().toByte()
    }
    
    fun readUByte(): UByte {
        return input.read().toUByte()
    }
    
    fun readBytes(buffer: ByteArray, size: Int, offset: Int): Int {
        return input.read(buffer, offset, size)
    }
    
    fun readBytes(buffer: ByteArray, size: Int): Int {
        return readBytes(buffer, size, 0)
    }
    
    fun readBytes(buffer: ByteArray): Int {
        return readBytes(buffer, buffer.size)
    }
    
    fun readBytes(size: Int): ByteArray {
        return ByteArray(size).apply { readBytes(this) }
    }
    
    fun readBoolean(): Boolean {
        return readByte().toInt() != 0
    }
    
    fun readBoolean(length: Int): Boolean {
        return readBytes(length).fold(0) { acc, byte -> acc or byte.toInt() } != 0
    }
    
    fun readChar(): Char {
        return readShort().toInt().toChar()
    }
    
    fun readShort(): Short {
        val bytes = readBytes(2)
        
        return if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            (bytes[1].toInt() shl 8) or (bytes[0].toInt() and 0xFF)
        } else {
            (bytes[0].toInt() shl 8) or (bytes[1].toInt() and 0xFF)
        }.toShort()
    }
    
    fun readUShort(): UShort {
        val bytes = readBytes(2)

        return if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            (bytes[1].toInt() shl 8) or (bytes[0].toInt() and 0xFF)
        } else {
            (bytes[0].toInt() shl 8) or (bytes[1].toInt() and 0xFF)
        }.toUShort()
    }

    fun readInt(): Int {
        val bytes = readBytes(4)

        return if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            (bytes[3].toInt() shl 24) or
                    ((bytes[2].toInt() and 0xFF) shl 16) or
                    ((bytes[1].toInt() and 0xFF) shl 8) or
                    (bytes[0].toInt() and 0xFF)
        } else {
            (bytes[0].toInt() shl 24) or
                    ((bytes[1].toInt() and 0xFF) shl 16) or
                    ((bytes[2].toInt() and 0xFF) shl 8) or
                    (bytes[3].toInt() and 0xFF)
        }
    }

    fun readUInt(): UInt {
        val bytes = readBytes(4)

        return if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            (bytes[3].toUInt() shl 24) or
           ((bytes[2].toUInt() and 0xFFu) shl 16) or
           ((bytes[1].toUInt() and 0xFFu) shl 8) or
            (bytes[0].toUInt() and 0xFFu)
        } else {
            (bytes[0].toUInt() shl 24) or
           ((bytes[1].toUInt() and 0xFFu) shl 16) or
           ((bytes[2].toUInt() and 0xFFu) shl 8) or
            (bytes[3].toUInt() and 0xFFu)
        }
    }
    
    fun readLong(): Long {
        val bytes = readBytes(8)
        
        return if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
             (bytes[7].toLong() shl 56) or 
            ((bytes[6].toLong() and 0xFF) shl 48) or 
            ((bytes[5].toLong() and 0xFF) shl 40) or 
            ((bytes[4].toLong() and 0xFF) shl 32) or 
            ((bytes[3].toLong() and 0xFF) shl 24) or 
            ((bytes[2].toLong() and 0xFF) shl 16) or 
            ((bytes[1].toLong() and 0xFF) shl 8) or 
             (bytes[0].toLong() and 0xFF)
        } else {
             (bytes[0].toLong() shl 56) or 
            ((bytes[1].toLong() and 0xFF) shl 48) or 
            ((bytes[2].toLong() and 0xFF) shl 40) or 
            ((bytes[3].toLong() and 0xFF) shl 32) or 
            ((bytes[4].toLong() and 0xFF) shl 24) or 
            ((bytes[5].toLong() and 0xFF) shl 16) or 
            ((bytes[6].toLong() and 0xFF) shl 8) or 
             (bytes[7].toLong() and 0xFF)
        }
    }
    
    fun readULong(): ULong {
        val bytes = readBytes(8)
        
        return if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
             (bytes[7].toULong() shl 56) or 
            ((bytes[6].toULong() and 0xFFu) shl 48) or 
            ((bytes[5].toULong() and 0xFFu) shl 40) or 
            ((bytes[4].toULong() and 0xFFu) shl 32) or 
            ((bytes[3].toULong() and 0xFFu) shl 24) or 
            ((bytes[2].toULong() and 0xFFu) shl 16) or 
            ((bytes[1].toULong() and 0xFFu) shl 8) or 
             (bytes[0].toULong() and 0xFFu)
        } else {
             (bytes[0].toULong() shl 56) or 
            ((bytes[1].toULong() and 0xFFu) shl 48) or 
            ((bytes[2].toULong() and 0xFFu) shl 40) or 
            ((bytes[3].toULong() and 0xFFu) shl 32) or 
            ((bytes[4].toULong() and 0xFFu) shl 24) or 
            ((bytes[5].toULong() and 0xFFu) shl 16) or 
            ((bytes[6].toULong() and 0xFFu) shl 8) or 
             (bytes[7].toULong() and 0xFFu)
        }
    }
    
    fun readFloat(): Float {
        return Float.fromBits(readInt())
    }
    
    fun readDouble(): Double {
        return Double.fromBits(readLong())
    }

    fun readString(): String {
        val length = readInt()
        return String(readBytes(length))
    }

    fun readChars(count: Int): String {
        return String(readBytes(count))
    }
    
    fun skip(size: Int) {
        input.skip(size.toLong())
    }

    fun available(): Int {
        return input.available()
    }
    
    fun close() {
        input.close()
    }

}

fun InputStream.asBinaryInputStream() = BinaryInputStream(this)