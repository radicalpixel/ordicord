package com.pixelized.ordiscord.engine

data class Command(
        val description: String,
        val keywords: String,
        val short: String? = null,
        val long: String? = null,
        val arg: Boolean = false
)