package com.pixelized.ordiscord.network.dto.worldstate

import com.google.gson.annotations.SerializedName

data class Date(
        @SerializedName("\$date")
        val date: NumberLong
)