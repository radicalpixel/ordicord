package com.pixelized.ordiscord.model.config

import com.google.gson.annotations.SerializedName

data class Config(
        @SerializedName("itemsPath")
        val itemsPath: String,
        @SerializedName("channel")
        val channel: String
)