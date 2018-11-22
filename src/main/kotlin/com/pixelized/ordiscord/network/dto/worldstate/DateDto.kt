package com.pixelized.ordiscord.network.dto.worldstate

import com.google.gson.annotations.SerializedName

data class DateDto(
        @SerializedName("\$date")
        val date: NumberLongDto
)