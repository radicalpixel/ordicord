package com.pixelized.ordiscord.engine.config

import com.google.gson.annotations.SerializedName

data class Config(
        @SerializedName("itemsPath")
        val itemsPath: String,
        @SerializedName("channel")
        val channel: String
)