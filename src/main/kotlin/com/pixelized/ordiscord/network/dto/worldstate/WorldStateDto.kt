package com.pixelized.ordiscord.network.dto.worldstate

import com.google.gson.annotations.SerializedName

data class WorldStateDto(
        @SerializedName("Time")
        val time: Long,
        @SerializedName("Date")
        val date: Long,
        @SerializedName("Alerts")
        val alerts: List<AlertDto>
)





