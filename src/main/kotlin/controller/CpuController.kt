@file:OptIn(ExperimentalStdlibApi::class)

package controller

import cpu.Cpu
import getBits
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val program = uintArrayOf(
    0x1_0000A_0_0u,
    0x2_00000_1_0u,
    0x1_00001_2_0u,
    0x4_00000_2_3u,
    0xB_0000C_1_3u,
    0x7_00007_0_0u,
    0x0_00000_3_1u,
    0xB_0000A_2_0u,
    0xB_0000C_2_0u,
    0x8_00003_0_0u,
    0x9_00000_1_0u,
    0xA_00000_0_0u,
)
private val data = uintArrayOf(
    5u, 4u, 7u, 10u, 20u, 1u, 0u, 3u, 8u, 9u,
)

object CpuController {

    private lateinit var cpu: Cpu
    private var executionJob: Job? = null
    private var executionDelay = 500L
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

    fun updateState() {
        _screenState.value = _screenState.value.copy(
            output = cpu.output,
            isHalted = cpu.flag.getBits(2, 2) == 1u,
            commands = cpu.commandRAM.filter { it.code != 0u }.mapIndexed { index, value ->
                Command(
                    index = index,
                    hexValue = value.code.toHexString(),
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