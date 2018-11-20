package com.pixelized.ordiscord.network.dto

import com.google.gson.annotations.SerializedName

data class WorldState(
        @SerializedName("Time")
        val time: Long,
        @SerializedName("Date")
        val date: Long,
        @SerializedName("Alerts")
        val alerts: List<Alerts>
)





