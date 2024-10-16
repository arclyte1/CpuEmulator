package cpu.model

enum class Instruction(val code: UInt) {
    MOV(0b0000u),
    LDV(0b0001u),
    LDA(0b0010u),
    LDM(0b0011u),
    LDR(0b0100u),
    LDP(0b0101u),
    JMP(0b0110u),
    JG (0b0111u),
    JL (0b1000u),
    OUT(0b1001u),
    HLT(0b1010u),
    ATH(0b1011u),
}