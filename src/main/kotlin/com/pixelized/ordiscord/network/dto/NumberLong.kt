package com.pixelized.ordiscord.network.dto

import com.google.gson.annotations.SerializedName

data class NumberLong(
        @SerializedName("\$numberLong")
        val long: Long
)