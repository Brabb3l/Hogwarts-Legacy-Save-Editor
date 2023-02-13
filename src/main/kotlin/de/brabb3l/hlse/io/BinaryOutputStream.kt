package de.brabb3l.hlse.io

import java.io.OutputStream
import java.nio.ByteOrder

class BinaryOutputStream(private val output: OutputStream) {
    
    val byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN
    
    fun writeByte(value: Byte) {
        output.write(value.toInt())
    }
    
    fun writeUByte(value: UByte) {
        output.write(value.toInt())
    }
    
    fun writeBytes(buffer: ByteArray, size: Int, offset: Int) {
        output.write(buffer, offset, size)
    }
    
    fun writeBytes(buffer: ByteArray, size: Int) {
        writeBytes(buffer, size, 0)
    }
    
    fun writeBytes(buffer: ByteArray) {
        writeBytes(buffer, buffer.size)
    }

    fun writeBoolean(value: Boolean) {
        writeByte(if (value) 1 else 0)
    }
    
    fun writeBoolean(value: Boolean, size: Int) {
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            writeByte(if (value) 1 else 0)
            
            for (i in 0 until size - 1) {
                writeByte(0)
            }
        } else {
            for (i in 0 until size - 1) {
                writeByte(0)
            }
            
            writeByte(if (value) 1 else 0)
        }
    }
    
    fun writeChar(value: Char) {
        writeShort(value.code.toShort())
    }
    
    fun writeShort(value: Short) {
        val bytes = ByteArray(2)
        
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            bytes[0] = (value.toInt() and 0xFF).toByte()
            bytes[1] = (value.toInt() shr 8).toByte()
        } else {
            bytes[0] = (value.toInt() shr 8).toByte()
            bytes[1] = (value.toInt() and 0xFF).toByte()
        }
        
        writeBytes(bytes)
    }
    
    fun writeUShort(value: UShort) {
        writeShort(value.toShort())
    }
    
    fun writeInt(value: Int) {
        val bytes = ByteArray(4)
        
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            bytes[0] = (value and 0xFF).toByte()
            bytes[1] = (value shr 8 and 0xFF).toByte()
            bytes[2] = (value shr 16 and 0xFF).toByte()
            bytes[3] = (value shr 24 and 0xFF).toByte()
        } else {
            bytes[0] = (value shr 24 and 0xFF).toByte()
            bytes[1] = (value shr 16 and 0xFF).toByte()
            bytes[2] = (value shr 8 and 0xFF).toByte()
            bytes[3] = (value and 0xFF).toByte()
        }
        
        writeBytes(bytes)
    }
    
    fun writeUInt(value: UInt) {
        writeInt(value.toInt())
    }
    
    fun writeLong(value: Long) {
        val bytes = ByteArray(8)
        
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            bytes[0] = (value and 0xFF).toByte()
            bytes[1] = (value shr 8 and 0xFF).toByte()
            bytes[2] = (value shr 16 and 0xFF).toByte()
            bytes[3] = (value shr 24 and 0xFF).toByte()
            bytes[4] = (value shr 32 and 0xFF).toByte()
            bytes[5] = (value shr 40 and 0xFF).toByte()
            bytes[6] = (value shr 48 and 0xFF).toByte()
            bytes[7] = (value shr 56 and 0xFF).toByte()
        } else {
            bytes[0] = (value shr 56 and 0xFF).toByte()
            bytes[1] = (value shr 48 and 0xFF).toByte()
            bytes[2] = (value shr 40 and 0xFF).toByte()
            bytes[3] = (value shr 32 and 0xFF).toByte()
            bytes[4] = (value shr 24 and 0xFF).toByte()
            bytes[5] = (value shr 16 and 0xFF).toByte()
            bytes[6] = (value shr 8 and 0xFF).toByte()
            bytes[7] = (value and 0xFF).toByte()
        }
        
        writeBytes(bytes)
    }
    
    fun writeULong(value: ULong) {
        writeLong(value.toLong())
    }
    
    fun writeFloat(value: Float) {
        writeInt(value.toRawBits())
    }
    
    fun writeDouble(value: Double) {
        writeLong(value.toRawBits())
    }
    
    fun writeString(value: String, size: Int) {
        val bytes = value.toByteArray()
        
        writeBytes(bytes, size)
    }
    
    fun writeString(value: String) {
        writeString(value, value.length)
    }
    
    fun writeChars(value: String, size: Int) {
        val bytes = value.toByteArray()
        
        // stretch to size
        if (bytes.size < size) {
            val newBytes = ByteArray(size)
            System.arraycopy(bytes, 0, newBytes, 0, bytes.size)
            writeBytes(newBytes)
        } else {
            writeBytes(bytes, size)
        }
    }
    
    fun skip(size: Int) {
        for (i in 0 until size) {
            writeByte(0)
        }
    }
    
    fun flush() {
        output.flush()
    }
    
    fun close() {
        output.close()
    }
    
}

fun OutputStream.asBinaryOutputStream() = BinaryOutputStream(this)