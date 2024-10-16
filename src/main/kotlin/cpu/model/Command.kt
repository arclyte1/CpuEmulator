package cpu.model

import getBits

data class Command(
    val instruction: Instruction,
    val literal: UInt,
    val firstOperand: UInt,
    val secondOperand: UInt,
    val code: UInt,
) {

    constructor(code: UInt) : this(
        instruction = Instruction.values().first { it.code == code.getBits(28, 31) },
        literal = code.getBits(8, 27),
        firstOperand = code.getBits(4, 7),
        secondOperand = code.getBits(0, 3),
        code = code,
    )
}