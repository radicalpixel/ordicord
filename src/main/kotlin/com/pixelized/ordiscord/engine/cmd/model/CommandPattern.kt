package com.pixelized.ordiscord.engine.cmd.model

data class CommandPattern(
        val id: String,
        val keyword: String,
        val options: List<OptionPattern>? = null
)