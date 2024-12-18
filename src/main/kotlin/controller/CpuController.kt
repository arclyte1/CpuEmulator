package controller

import InitialExecDelay
import cpu.Cpu
import getBits
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.swing.JFileChooser
import javax.swing.UIManager


@OptIn(ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class, DelicateCoroutinesApi::class)
object CpuController {

    private lateinit var cpu: Cpu
    private var executionJob: Job? = null
    var executionDelay = InitialExecDelay
        private set
    private val _screenState = MutableStateFlow(ScreenState())
    val screenState: StateFlow<ScreenState> = _screenState
    private var program = ""

    init {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        reset()
    }

    fun reset() {
        cpu = Cpu()
        cpu.loadProgram(program)
        pause()
        updateState()
    }

    private fun updateState() {
        var commandIndex = 0
        _screenState.value = _screenState.value.copy(
            output = cpu.output,
            isHalted = cpu.flag.getBits(2, 2) == 1u,
            commands = cpu.assemblerCommands.map {
                val index = it.command?.let {
                    commandIndex++
                }
                Command(
                    index = index,
                    assemblerCommand = it,
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

    fun selectFile() {
        val chooser = JFileChooser()
        val status = chooser.showOpenDialog(null)
        if (status == JFileChooser.APPROVE_OPTION) {
            program = chooser.selectedFile?.readText(charset = Charsets.UTF_8) ?: return
            reset()
        }
    }
}