package controller


data class ScreenState(
    val generalRegisters: List<Register> = emptyList(),
    val serviceRegisters: List<Register> = emptyList(),
    val commands: List<Command> = emptyList(),
    val output: List<String> = emptyList(),
    val isExecuting: Boolean = false,
    val isHalted: Boolean = false,
)

data class Register(
    val name: String,
    val value: String,
)

data class Command(
    val index: Int,
    val hexValue: String,
    val isCurrent: Boolean,
)