package cpu.model

enum class ArithmeticInstruction(val code: UInt){
    ADD(0b0000u),
    SUB(0b0001u),
    MUL(0b0010u),
    DIV(0b0011u),
    SHL(0b0100u),
    SHR(0b0101u),
    AND(0b0110u),
    OR (0b0111u),
    XOR(0b1000u),
    NOT(0b1001u),
    INC(0b1010u),
    DEC(0b1011u),
    CMP(0b1100u),
}