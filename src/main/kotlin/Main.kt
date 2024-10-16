import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import controller.CpuController


@Composable
@Preview
fun App() {
    val state by CpuController.screenState.collectAsState()

    MaterialTheme {
        Column {
            Row {
                Column {
                    (state.generalRegisters + state.serviceRegisters).forEachIndexed { index, value ->
                        Row {
                            Text(value.name, modifier = Modifier.width(60.dp))
                            Text(value.value)
                        }
                    }
                }
                Column(modifier = Modifier.padding(start = 40.dp)) {
                    state.commands.forEach {
                        val modifier = if (it.isCurrent) {
                            Modifier.background(color = Color( 0x7700FF00))
                        } else {
                            Modifier
                        }
                        Row(
                            modifier = modifier
                        ) {
                            Text(it.index.toString(), modifier = Modifier.width(40.dp), color = Color.Gray)
                            Text(it.hexValue)
                        }
                    }
                }
                Column(modifier = Modifier.padding(start = 40.dp)) {
                    state.output.forEach {
                        Text(it)
                    }
                }
            }
            Row {
                Button(onClick = { CpuController.reset() }) {
                    Text("Reset")
                }
                Button(onClick = { CpuController.next() }) {
                    Text("Next")
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

