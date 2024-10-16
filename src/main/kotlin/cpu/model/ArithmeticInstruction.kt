package cpu.model

enum class ArithmeticInstruction(val code: UInt){
    ADD(0b0000u),   // Addition
    SUB(0b0001u),   // Subtraction
    MUL(0b0010u),   // Multiplication
    DIV(0b0011u),   // Division
    SHL(0b0100u),   // Left shift
    SHR(0b0101u),   // Right shift
    AND(0b0110u),   // And
    OR (0b0111u),   // Or
    XOR(0b1000u),   // Xor
    NOT(0b1001u),   // Not
    INC(0b1010u),   // Increment
    DEC(0b1011u),   // Decrement
    CMP(0b1100u),   // Comparison
}