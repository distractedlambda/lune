package org.lunelang.language.compiler

import com.oracle.truffle.api.memory.ByteArraySupport
import java.util.Objects.checkFromIndexSize
import java.util.Objects.checkIndex

class ByteArrayList(capacity: Int = 0) {
    private var buffer: ByteArray
    private var size: Int = 0

    init {
        require(capacity >= 0)
        buffer = if (capacity == 0) EMPTY_BUFFER else ByteArray(capacity)
    }

    fun toByteArray(): ByteArray {
        return buffer.copyOf(size)
    }

    operator fun get(index: Int): Byte {
        checkIndex(index, size)
        return buffer[index]
    }

    fun getShort(byteOffset: Int): Short {
        checkFromIndexSize(byteOffset, 2, size)
        return BYTE_ARRAY_SUPPORT.getShort(buffer, byteOffset)
    }

    fun getInt(byteOffset: Int): Int {
        checkFromIndexSize(byteOffset, 4, size)
        return BYTE_ARRAY_SUPPORT.getInt(buffer, byteOffset)
    }

    fun getLong(byteOffset: Int): Long {
        checkFromIndexSize(byteOffset, 8, size)
        return BYTE_ARRAY_SUPPORT.getLong(buffer, byteOffset)
    }

    fun getFloat(byteOffset: Int): Float {
        checkFromIndexSize(byteOffset, 4, size)
        return BYTE_ARRAY_SUPPORT.getFloat(buffer, byteOffset)
    }

    fun getDouble(byteOffset: Int): Double {
        checkFromIndexSize(byteOffset, 8, size)
        return BYTE_ARRAY_SUPPORT.getDouble(buffer, byteOffset)
    }

    operator fun set(index: Int, value: Byte) {
        checkIndex(index, size)
        buffer[index] = value
    }

    fun setShort(byteOffset: Int, value: Short) {
        checkFromIndexSize(byteOffset, 2, size)
        BYTE_ARRAY_SUPPORT.putShort(buffer, byteOffset, value)
    }

    fun setInt(byteOffset: Int, value: Int) {
        checkFromIndexSize(byteOffset, 4, size)
        BYTE_ARRAY_SUPPORT.putInt(buffer, byteOffset, value)
    }

    fun setLong(byteOffset: Int, value: Long) {
        checkFromIndexSize(byteOffset, 8, size)
        BYTE_ARRAY_SUPPORT.putLong(buffer, byteOffset, value)
    }

    fun setFloat(byteOffset: Int, value: Float) {
        checkFromIndexSize(byteOffset, 4, size)
        BYTE_ARRAY_SUPPORT.putFloat(buffer, byteOffset, value)
    }

    fun setDouble(byteOffset: Int, value: Double) {
        checkFromIndexSize(byteOffset, 8, size)
        BYTE_ARRAY_SUPPORT.putDouble(buffer, byteOffset, value)
    }

    private fun ensureAdditionalSpace(additionalSpace: Int) {
        if (Int.MAX_VALUE - additionalSpace < size) {
            error("Minimum capacity would overflow an integer")
        }

        val minimumCapacity = size + additionalSpace

        if (buffer.size >= minimumCapacity) {
            return
        }

        val newCapacity = maxOf(buffer.size + buffer.size / 2, minimumCapacity)
        buffer = buffer.copyOf(newCapacity)
    }

    fun appendByte(byte: Byte) {
        ensureAdditionalSpace(1)
        buffer[size++] = byte
    }

    fun appendShort(value: Short) {
        ensureAdditionalSpace(2)
        BYTE_ARRAY_SUPPORT.putShort(buffer, size, value)
        size += 2
    }

    fun appendInt(value: Int) {
        ensureAdditionalSpace(4)
        BYTE_ARRAY_SUPPORT.putInt(buffer, size, value)
        size += 4
    }

    fun appendLong(value: Long) {
        ensureAdditionalSpace(8)
        BYTE_ARRAY_SUPPORT.putLong(buffer, size, value)
        size += 8
    }

    fun appendFloat(value: Float) {
        ensureAdditionalSpace(4)
        BYTE_ARRAY_SUPPORT.putFloat(buffer, size, value)
        size += 4
    }

    fun appendDouble(value: Double) {
        ensureAdditionalSpace(8)
        BYTE_ARRAY_SUPPORT.putDouble(buffer, size, value)
        size += 8
    }

    companion object {
        private val EMPTY_BUFFER = byteArrayOf()
        private val BYTE_ARRAY_SUPPORT = ByteArraySupport.littleEndian()
    }
}
