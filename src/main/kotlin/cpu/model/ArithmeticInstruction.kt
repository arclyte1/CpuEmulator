package cpu.model

enum class ArithmeticInstruction(val code: UInt){
    ADD(0b0000u),   // Addition         2
    SUB(0b0001u),   // Subtraction      2
    MUL(0b0010u),   // Multiplication   2
    DIV(0b0011u),   // Division         2
    SHL(0b0100u),   // Left shift       2
    SHR(0b0101u),   // Right shift      2
    AND(0b0110u),   // And              2
    OR (0b0111u),   // Or               2
    XOR(0b1000u),   // Xor              2
    NOT(0b1001u),   // Not              1
    INC(0b1010u),   // Increment        1
    DEC(0b1011u),   // Decrement        1
    CMP(0b1100u),   // Comparison       2
}