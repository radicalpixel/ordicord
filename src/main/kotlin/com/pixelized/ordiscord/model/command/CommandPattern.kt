package com.pixelized.ordiscord.model.command

data class CommandPattern(
        val id: String,
        val keyword: String,
        val options: List<OptionPattern>? = null
)