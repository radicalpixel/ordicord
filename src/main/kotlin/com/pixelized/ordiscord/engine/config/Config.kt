package com.pixelized.ordiscord.engine.config

import com.google.gson.annotations.SerializedName

data class Config(
        @SerializedName("discordChannel")
        val channel: String
)