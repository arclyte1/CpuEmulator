package cpu.model

enum class Instruction(val code: UInt) {
    MOV(0b0000u),   // Move from one reg to another (MOV r0, r1)
    LDV(0b0001u),   // Load exact value to reg (LDV 10, r1)
    LDA(0b0010u),   // Read value from memory by index to reg (LDA 5, r1)
    LDM(0b0011u),   // Write to memory by index from reg (LDM 5, r1)
    LDR(0b0100u),   // Read value from memory by index pointed in reg to another reg (LDR r0, r1)
    LDP(0b0101u),   // Write value to memory by index pointed in reg from another reg (LDR r0, r1)
    JMP(0b0110u),   // Jump to command (JMP 5)
    JG (0b0111u),   // Jump if greater flag == 1 (JG 5)
    JL (0b1000u),   // Jump if less flag == 1 (JL 5)
    OUT(0b1001u),   // Output reg value on screen (OUT r1)
    HLT(0b1010u),   // Stop execution (HLT)
    ATH(0b1011u),   // Arithmetic operation, aliases in ArithmeticInstruction (ADD r1, r2)
}