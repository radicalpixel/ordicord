package com.pixelized.ordiscord.network.dto

import com.google.gson.annotations.SerializedName

data class Date(
        @SerializedName("\$date")
        val date: NumberLong
)