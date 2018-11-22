package com.pixelized.ordiscord.model.command

data class Command(
        val id: String,
        val options: MutableList<Option>? = null
)