@file:OptIn(ExperimentalUnsignedTypes::class)

package cpu

import cpu.model.ArithmeticInstruction
import cpu.model.Command
import cpu.model.Instruction
import get
import set
import setBit
import shl
import shr

class Cpu {

    val output = mutableListOf<String>()

    var flag = 0b0_0_0u
    var pc = 0u
    val reg = UIntArray(16)
    val commandRAM: Array<Command> = Array(1024) { Command(0u) }
    val dataRAM = UIntArray(1024)

    fun loadProgram(program: UIntArray, data: UIntArray) {
        program.forEachIndexed { index, command ->
            commandRAM[index] = Command(command)
        }
        data.forEachIndexed { index, value ->
            dataRAM[index] = value
        }
    }

    fun executeCommand() {
        val cmd = commandRAM[pc]
        when (cmd.instruction) {
            Instruction.MOV -> {
                reg[cmd.secondOperand] = reg[cmd.firstOperand]
                pc++
            }

            Instruction.LDV -> {
                reg[cmd.firstOperand] = cmd.literal
                pc++
            }

            Instruction.LDA -> {
                reg[cmd.firstOperand] = dataRAM[cmd.literal]
                pc++
            }

            Instruction.LDM -> {
                dataRAM[cmd.literal] = reg[cmd.firstOperand]
                pc++
            }

            Instruction.LDR -> {
                reg[cmd.secondOperand] = dataRAM[reg[cmd.firstOperand]]
                pc++
            }

            Instruction.LDP -> {
                dataRAM[reg[cmd.secondOperand]] = reg[cmd.firstOperand]
                pc++
            }

            Instruction.JMP -> {
                pc = cmd.literal
            }

            Instruction.JG -> {
                if (flag == 1u) {
                    pc = cmd.literal
                } else {
                    pc++
                }
            }

            Instruction.JL -> {
                if (flag == 2u) {
                    pc = cmd.literal
                } else {
                    pc++
                }
            }

            Instruction.OUT -> {
                output.add(reg[cmd.firstOperand].toString())
                pc++
            }

            Instruction.HLT -> {
                flag = flag.setBit(2, 1)
            }

            Instruction.ATH -> {
                val arithmeticInstruction = ArithmeticInstruction.entries.first { it.code == cmd.literal }
                when(arithmeticInstruction) {
                    ArithmeticInstruction.ADD -> reg[cmd.secondOperand] += reg[cmd.firstOperand]
                    ArithmeticInstruction.SUB -> reg[cmd.secondOperand] -= reg[cmd.firstOperand]
                    ArithmeticInstruction.MUL -> reg[cmd.secondOperand] *= reg[cmd.firstOperand]
                    ArithmeticInstruction.DIV -> reg[cmd.secondOperand] /= reg[cmd.firstOperand]
                    ArithmeticInstruction.SHL -> reg[cmd.secondOperand] = reg[cmd.secondOperand] shl reg[cmd.firstOperand]
                    ArithmeticInstruction.SHR -> reg[cmd.secondOperand] = reg[cmd.secondOperand] shr reg[cmd.firstOperand]
                    ArithmeticInstruction.AND -> reg[cmd.secondOperand] = reg[cmd.secondOperand] and reg[cmd.firstOperand]
                    ArithmeticInstruction.OR  -> reg[cmd.secondOperand] = reg[cmd.secondOperand] or reg[cmd.firstOperand]
                    ArithmeticInstruction.XOR -> reg[cmd.secondOperand] = reg[cmd.secondOperand] xor reg[cmd.firstOperand]
                    ArithmeticInstruction.NOT -> reg[cmd.firstOperand] = reg[cmd.firstOperand].inv()
                    ArithmeticInstruction.INC -> reg[cmd.firstOperand]++
                    ArithmeticInstruction.DEC -> reg[cmd.firstOperand]--
                    ArithmeticInstruction.CMP -> flag = if (reg[cmd.firstOperand] > reg[cmd.secondOperand]) {
                        flag.setBit(0, 1).setBit(1, 0)
                    } else if (reg[cmd.firstOperand] < reg[cmd.secondOperand]) {
                        flag.setBit(0, 0).setBit(1, 1)
                    } else {
                        flag.setBit(0, 0).setBit(0, 0)
                    }
                }
                pc++
            }
        }
    }
}