package com.pixelized.ordiscord.model.command

data class OptionPattern(
        val id: String,
        val description: String,
        val short: String,
        val long: String? = null,
        val mandatory: Boolean = false,
        val args: Int = 0
)