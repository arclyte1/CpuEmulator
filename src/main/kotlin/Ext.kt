fun UInt.getBits(start: Int, end: Int): UInt {
    return this shl (31 - end) shr (start + 31 - end)
}

fun UInt.toBinaryString(): String {
    var value = this
    val sb = StringBuilder()
    while (value != 0u) {
        sb.append(value % 2u).toString()
        value /= 2u
    }
    return if (sb.isEmpty()) {
        "0"
    } else {
        sb.reverse().toString()
    }
}

operator fun <T> Array<T>.get(index: UInt) = this[index.toInt()]

operator fun <T> Array<T>.set(index: UInt, value: T) = this.set(index.toInt(), value)

operator fun UIntArray.get(index: UInt) = this[index.toInt()]

operator fun UIntArray.set(index: UInt, value: UInt) = this.set(index.toInt(), value)

infix fun UInt.shl(bitCount: UInt): UInt = this.shl(bitCount.toInt())

infix fun UInt.shr(bitCount: UInt): UInt = this.shl(bitCount.toInt())

fun UInt.setBit(index: Int, value: Int): UInt {
    return if (value % 2 == 1) {
        this or (1u shl index)
    } else {
        this and (1u shl index).inv()
    }
}