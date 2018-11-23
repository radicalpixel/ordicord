package com.pixelized.ordiscord.model.command

data class Command(
        val id: String,
        val description: String? = null,
        val args: MutableList<String>? = null,
        val options: MutableList<Option>? = null
)