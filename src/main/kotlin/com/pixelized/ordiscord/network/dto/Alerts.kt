package com.pixelized.ordiscord.network.dto

import com.google.gson.annotations.SerializedName

data class Alerts(
        @SerializedName("Activation")
        val activation: Date,
        @SerializedName("Expiry")
        val expiry: Date,
        @SerializedName("MissionInfo")
        val missionInfo: MissionInfo
)