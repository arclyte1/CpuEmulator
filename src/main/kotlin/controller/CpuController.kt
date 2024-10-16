package controller

import cpu.Cpu
import getBits
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private val program = uintArrayOf(
    0x1_0000A_0_0u,     // LDV 10, r0
    0x2_00000_1_0u,     // LDA 0, r1
    0x1_00001_2_0u,     // LDV 1, r2
    0x4_00000_2_3u,     // LDR r2, r3
    0xB_0000C_1_3u,     // CMP r1, r3
    0x7_00007_0_0u,     // JG  7
    0x0_00000_3_1u,     // MOV r3, r1
    0xB_0000A_2_0u,     // INC r2
    0xB_0000C_2_0u,     // CMP r2, r0
    0x8_00003_0_0u,     // JL  3
    0x9_00000_1_0u,     // OUT r1
    0xA_00000_0_0u,     // HLT
)
private val data = uintArrayOf(
    5u, 4u, 7u, 10u, 20u, 1u, 0u, 3u, 8u, 9u,
)

@OptIn(ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class, DelicateCoroutinesApi::class)
object CpuController {

    private lateinit var cpu: Cpu
    private var executionJob: Job? = null
    var executionDelay = 500L
        private set
    private val _screenState = MutableStateFlow(ScreenState())
    val screenState: StateFlow<ScreenState> = _screenState

    init {
        reset()
    }

    fun reset() {
        cpu = Cpu()
        cpu.loadProgram(program, data)
        pause()
        updateState()
    }

    private fun updateState() {
        _screenState.value = _screenState.value.copy(
            output = cpu.output,
            isHalted = cpu.flag.getBits(2, 2) == 1u,
            commands = cpu.commandRAM.filter { it != 0u }.mapIndexed { index, value ->
                Command(
                    index = index,
                    hexValue = value.toHexString(),
                    isCurrent = index == cpu.pc.toInt()
                )
            },
            generalRegisters = cpu.reg.mapIndexed { index, value ->
                Register(
                    name = "R$index",
                    value = value.toHexString(),
                )
            },
            serviceRegisters = listOf(
                Register(
                    name = "PC",
                    value = cpu.pc.toHexString(),
                ),
                Register(
                    name = "FLAG",
                    value = cpu.flag.toHexString(),
                ),
            )
        )
    }

    fun setExecutionDelay(millis: Long) {
        executionDelay = millis
        if (executionJob != null) {
            executionJob?.cancel()
            executionJob = null
            resume()
        }
    }

    fun resume() {
        if (!_screenState.value.isHalted && executionJob == null) {
            _screenState.value = _screenState.value.copy(
                isExecuting = true,
            )
            executionJob = GlobalScope.launch {
                while (!_screenState.value.isHalted) {
                    delay(executionDelay)
                    cpu.executeCommand()
                    updateState()
                }
            }
        }
    }

    fun pause() {
        _screenState.value = _screenState.value.copy(
            isExecuting = false,
        )
        executionJob?.cancel()
        executionJob = null
    }

    fun next() {
        if (!_screenState.value.isHalted) {
            cpu.executeCommand()
            updateState()
        }
    }
}