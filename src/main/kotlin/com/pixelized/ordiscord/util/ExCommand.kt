package com.pixelized.ordiscord.util

import com.pixelized.ordiscord.model.command.Command
import com.pixelized.ordiscord.model.command.Option

infix fun Command.haveOption(optionId: String): Boolean = options?.any { it.id == optionId } == true

fun Command.getOption(optionId: String): Option = options?.find { it.id == optionId } as Option