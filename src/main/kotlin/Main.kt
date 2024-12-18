import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import controller.Command
import controller.CpuController
import controller.Register


@Composable
@Preview
fun App() {
    val state by CpuController.screenState.collectAsState()

    MaterialTheme {
        Column {
            Row(modifier = Modifier.padding(top = 16.dp).weight(1f)) {
                RegistersBlock(state.generalRegisters + state.serviceRegisters)
                ProgramBlock(state.commands)
            }
            OutputBlock(state.output)
            ActionsBlock(
                isExecuting = state.isExecuting,
                isHalted = state.isHalted,
                next = CpuController::next,
                reset = CpuController::reset,
                resume = CpuController::resume,
                pause = CpuController::pause,
                selectFile = CpuController::selectFile,
                setExecutionDelay = CpuController::setExecutionDelay,
            )
        }
    }
}

@Composable
private fun RegistersBlock(
    registers: List<Register>,
) {
    Column(modifier = Modifier.padding(start = 16.dp)) {
        Text("Регистры", fontWeight = FontWeight.Bold)
        LazyColumn(
            modifier = Modifier
                .padding(top = 8.dp, end = 16.dp)
                .border(1.dp, Color.Gray)
        ) {
            registers.forEachIndexed { index, value ->
                item {
                    val modifier = if (index % 2 == 0) Modifier.background(MaterialTheme.colors.primary.copy(alpha = 0.2f)) else Modifier
                    Row(
                        modifier = modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(value.name, modifier = Modifier.width(60.dp))
                        Text("0x${value.value.uppercase()}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
private fun ProgramBlock(
    commands: List<Command>,
) {
    Column {
        Text("Программа", fontWeight = FontWeight.Bold)
        Column(
            modifier = Modifier
                .padding(top = 8.dp, end = 16.dp)
                .border(1.dp, Color.Gray)
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.background(MaterialTheme.colors.primary.copy(alpha = 0.2f))) {
                Text(
                    text = "i",
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp).padding(end = 8.dp),
                )
                Text(
                    text = "Assembler",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Hex code",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(100.dp)
                )
            }
            Divider(modifier = Modifier.fillMaxWidth().height(1.5.dp), color = Color.Gray)
            LazyColumn {
                commands.forEachIndexed { index, command ->
                    item {
                        val modifier = if (command.isCurrent) {
                            Modifier.background(color = Color(0x7700FF00))
                        } else if (index % 2 == 0) {
                            Modifier.background(color = Color.LightGray.copy(alpha = 0.5f))
                        } else {
                            Modifier
                        }
                        Row(
                            modifier = modifier
                        ) {
                            Text(
                                text = command.index?.toString() ?: "",
                                color = Color.Gray,
                                textAlign = TextAlign.End,
                                modifier = Modifier.width(32.dp).padding(end = 8.dp),
                            )
                            Text(command.assemblerCommand.text, modifier = Modifier.weight(1f))
                            Text(
                                text = command.assemblerCommand.command?.toHexString()
                                    ?.let { "0x${it.uppercase()}" } ?: "",
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OutputBlock(
    output: List<String>,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Вывод", fontWeight = FontWeight.Bold)
        LazyColumn(
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
                .padding(top = 8.dp)
                .border(1.dp, Color.Gray)
        ) {
            items(output) {
                Text(it, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun ActionsBlock(
    isExecuting: Boolean,
    isHalted: Boolean,
    reset: () -> Unit,
    next: () -> Unit,
    resume: () -> Unit,
    pause: () -> Unit,
    selectFile: () -> Unit,
    setExecutionDelay: (Long) -> Unit,
) {
    var executionDelay by remember { mutableStateOf(CpuController.executionDelay) }
    val initialExecDelaySlider = (InitialExecDelay - MinExecDelay).toFloat() / (MaxExecDelay - MinExecDelay)
    var executionDelaySlider by remember { mutableStateOf(initialExecDelaySlider) }
    Row(modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)) {
        IconButton(onClick = reset) {
            Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colors.primary.copy(alpha = 0.5f))
        }
        IconButton(onClick = next) {
            val tint = if (!isHalted) {
                MaterialTheme.colors.primary.copy(alpha = 0.5f)
            } else {
                Color.Gray.copy(alpha = 0.5f)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = tint)
        }
        IconButton(onClick = resume) {
            val tint = if (!isHalted && !isExecuting) {
                MaterialTheme.colors.primary.copy(alpha = 0.5f)
            } else {
                Color.Gray.copy(alpha = 0.5f)
            }
            Icon(Icons.Default.PlayArrow, null, tint = tint)
        }
        IconButton(onClick = pause) {
            val tint = if (!isHalted && isExecuting) {
                MaterialTheme.colors.primary.copy(alpha = 0.5f)
            } else {
                Color.Gray.copy(alpha = 0.5f)
            }
            Icon(Icons.Default.Pause, null, tint = tint)
        }
        Slider(
            value = executionDelaySlider,
            onValueChange = {
                executionDelaySlider = it
                executionDelay = MinExecDelay + (it * (MaxExecDelay - MinExecDelay)).toLong()
                setExecutionDelay(executionDelay)
            },
            modifier = Modifier.width(200.dp)
        )
        Text(
            "$executionDelay ms",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        IconButton(onClick = selectFile) {
            Icon(
                Icons.Default.FileOpen,
                contentDescription = null,
                tint = MaterialTheme.colors.primary.copy(alpha = 0.5f)
            )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}


const val MinExecDelay = 5L
const val MaxExecDelay = 3000L
const val InitialExecDelay = 500L
