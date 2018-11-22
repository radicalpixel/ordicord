package com.pixelized.ordiscord.network.dto.worldstate

import com.google.gson.annotations.SerializedName

data class NumberLongDto(
        @SerializedName("\$numberLong")
        val long: Long
)