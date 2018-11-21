package com.pixelized.ordiscord.engine.cmd.model

data class Command(
        val id: String,
        val options: MutableList<Option>? = null
)