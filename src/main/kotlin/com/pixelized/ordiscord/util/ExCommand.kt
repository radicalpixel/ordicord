package com.pixelized.ordiscord.util

import com.pixelized.ordiscord.model.command.Command

infix fun Command.haveOption(optionId: String): Boolean = options?.any { it.id == optionId } == true