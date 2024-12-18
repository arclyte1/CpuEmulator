package assembler

import cpu.Cpu
import cpu.model.ArithmeticInstruction
import cpu.model.Instruction

@OptIn(ExperimentalUnsignedTypes::class)
object Assembler {

    // CPU-executable commands
    private val program = mutableListOf<UInt>()

    // CPU data
    private val data = MutableList(Cpu.DATA_RAM_SIZE) { 0u }

    // Command list for UI
    private val commands = mutableListOf<AssemblerCommand>()

    // Program arrays (array name to it ram start index)
    private val dataArrays = mutableMapOf<String, Int>()

    // Current size of written arrays
    private var dataSize = 0

    // Program labels for jumps (label name to command index)
    private val labels = mutableMapOf<String, Int>()

    fun parseProgram(text: String): ParseResult {
        program.clear()
        data.replaceAll { 0u }
        commands.clear()
        dataArrays.clear()
        dataSize = 0
        labels.clear()

        val lines = text.split("\n").map { it.replace("\r", "") }

        readLabelsAndData(lines)

        println(labels)

        lines.forEach { line ->
            println(line)
            commands.add(AssemblerCommand(line.uppercase(), parseLine(line)))
        }

        return ParseResult(
            program = program.toUIntArray(),
            data = data.toUIntArray(),
            commands = commands,
        )
    }

    private fun readLabelsAndData(lines: List<String>) {
        var commandIndex = 0
        lines.forEach {
            when {
                it.startsWith("DATA", ignoreCase = true) -> {
                    val args = it.replace('=', ' ')
                        .replace("{", "")
                        .replace("}", "")
                        .split(regex = Regex("[ ,]+"))
                        .toMutableList()
                    args.removeFirst()
                    dataArrays[args.removeFirst()] = dataSize
                    args.map(String::toUInt).forEach { value ->
                        data[dataSize] = value
                        dataSize++
                    }
                }

                it.startsWith('.')                        -> {
                    labels[it] = commandIndex
                }

                else                                           -> {
                    commandIndex++
                }
            }
        }
    }

    private fun parseLine(line: String): UInt? {
        var mLine = line
        if (line.startsWith(".") || line.startsWith("DATA", ignoreCase = true)) return null

        labels.forEach { (label, index) ->
            mLine = mLine.replace(label, index.toString())
        }
        dataArrays.forEach { (arrayName, index) ->
            mLine = mLine.replace("offset $arrayName", "$index", ignoreCase = true)
        }

        val words = mLine.split(regex = Regex("[ ,]+")).toMutableList()
        val instruction = words.removeFirst()
        return parseInstruction(instruction, words)
    }

    private fun parseInstruction(instruction: String, args: List<String>): UInt? {
        var inst = 0u
        var lit = 0u
        var op1 = 0u
        var op2 = 0u
        when (instruction) {
            Instruction.HLT.name           -> {
                inst = Instruction.HLT.code
            }

            Instruction.JMP.name,
            Instruction.JG.name,
            Instruction.JL.name            -> {
                inst = Instruction.valueOf(instruction).code
                lit = args[0].toUInt()
            }

            Instruction.OUT.name           -> {
                inst = Instruction.OUT.code
                op1 = args[0].replace("r", "", ignoreCase = true).toUInt()
            }

            Instruction.LDV.name,
            Instruction.LDA.name,
            Instruction.LDM.name           -> {
                inst = Instruction.valueOf(instruction).code
                lit = args[0].toUInt()
                op1 = args[1].replace("r", "", ignoreCase = true).toUInt()
            }

            Instruction.MOV.name,
            Instruction.LDR.name,
            Instruction.LDP.name           -> {
                inst = Instruction.valueOf(instruction).code
                op1 = args[0].replace("r", "", ignoreCase = true).toUInt()
                op2 = args[1].replace("r", "", ignoreCase = true).toUInt()
            }

            ArithmeticInstruction.ADD.name,
            ArithmeticInstruction.SUB.name,
            ArithmeticInstruction.MUL.name,
            ArithmeticInstruction.DIV.name,
            ArithmeticInstruction.SHL.name,
            ArithmeticInstruction.SHR.name,
            ArithmeticInstruction.AND.name,
            ArithmeticInstruction.OR.name,
            ArithmeticInstruction.XOR.name,
            ArithmeticInstruction.CMP.name -> {
                inst = Instruction.ATH.code
                lit = ArithmeticInstruction.valueOf(instruction).code
                op1 = args[0].replace("r", "", ignoreCase = true).toUInt()
                op2 = args[1].replace("r", "", ignoreCase = true).toUInt()
            }

            ArithmeticInstruction.NOT.name,
            ArithmeticInstruction.INC.name,
            ArithmeticInstruction.DEC.name -> {
                inst = Instruction.ATH.code
                lit = ArithmeticInstruction.valueOf(instruction).code
                op1 = args[0].replace("r", "", ignoreCase = true).toUInt()
            }

            else -> {}
        }
        val cmd = (inst shl 28) or (lit shl 8) or (op1 shl 4) or op2
        return if (cmd == 0u) {
            null
        } else {
            program.add(cmd)
            cmd
        }
    }

    data class ParseResult(
        val program: UIntArray,
        val data: UIntArray,
        val commands: List<AssemblerCommand>,
    )
}