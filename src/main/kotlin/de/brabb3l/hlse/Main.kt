package de.brabb3l.hlse

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import java.io.File

enum class Mode {
    PACK,
    UNPACK
}

fun main(args: Array<String>) {
    val parser = ArgParser("hlse")
    val mode by parser.argument(ArgType.Choice<Mode>(), description = "Mode")
    val input by parser.argument(ArgType.String, description = "Input file (or directory if mode is pack)")
    val output by parser.argument(ArgType.String, description = "Output file (or directory if mode is unpack)")

    parser.parse(args)

    val inputFile = File(input)
    val outputFile = File(output)

    when (mode) {
        Mode.PACK -> SaveManager.pack(inputFile, outputFile)
        Mode.UNPACK -> SaveManager.unpack(inputFile, outputFile)
    }

}