package com.example.activity_tracker

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class Coordinates(
    val n: Int,
    val x: Int,
    val y: Int,
    val z: Int
)


class PayloadParser {
    private val SIZE = 127
    private val PAYLOAD_SIZE = 20
    private val START = "start0"

    fun parse(line: ByteArray): List<Coordinates> = try {
        parseLine(line)
    } catch (e: Exception) {
        emptyList()
    }

    /**
     * Data schema:
     * line = 'start0'+N+payload
     * N(byte) is a message counter
     * Payload is a sequence of 2-byte numbers in the form [(x,y,z),(x,y,z),....]
     * Payload size is 120 bytes, so it's 60 numbers, so 20 Measurements
     */
    private fun parseLine(line: ByteArray): List<Coordinates> {
        if (line.size != SIZE) {
            return emptyList()
        }
        val bb = ByteBuffer.wrap(line)
        bb.order(ByteOrder.LITTLE_ENDIAN)
        START.forEach { c ->
            if (bb.get() != c.toByte()) {
                return emptyList()
            }
        }
        val n = bb.get().toInt() and 0xFF // number of block

        val sb = bb.asShortBuffer()
        return (0 until PAYLOAD_SIZE).map {
            Coordinates(n, sb.get().toInt(), sb.get().toInt(), sb.get().toInt())
        }
    }
}

class Parser(private val bufferSize: Int = 2048) {
    private val buffer = ByteArray(bufferSize)
    private var offset = 0
    private var lastIndex = -1

    fun parse(buf: ByteArray, size: Int): List<ByteArray> {
        if (size <= 0) return emptyList()
        if (size + offset > buffer.size) {
            reset()
        }

        buf.copyInto(buffer, offset, 0, size)
        offset += size

        val lines = arrayListOf<ByteArray>()
        for (i in 0 until offset - 1) {
            val cond = buffer[i] == '\n'.toByte() && buffer[i + 1] == '\r'.toByte()
            if (!cond) continue
            if (lastIndex == -1) {
                lastIndex = i + 2
                continue
            }
            if (i - lastIndex != 0) {
                val line = buffer.copyOfRange(lastIndex, i)
                lines.add(line)
            }
            lastIndex = i + 2
        }

        if (lastIndex == -1) {
            reset()
        } else {
            shift(buffer, lastIndex, offset + 1)
            offset -= lastIndex
            lastIndex = 0
        }

        return lines
    }

    private fun shift(src: ByteArray, n: Int, size: Int = src.size) {
        val tmp = ByteArray(src.size)
        src.copyInto(tmp, 0, n, size)
        tmp.copyInto(src)
    }

    private fun reset() {
        buffer.fill(0)
        lastIndex = -1
        offset = 0
    }
}