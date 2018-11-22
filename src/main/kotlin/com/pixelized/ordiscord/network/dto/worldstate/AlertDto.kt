package com.pixelized.ordiscord.network.dto.worldstate

import com.google.gson.annotations.SerializedName

data class AlertDto(
        @SerializedName("Activation")
        val activation: DateDto,
        @SerializedName("Expiry")
        val expiry: DateDto,
        @SerializedName("MissionInfo")
        val missionInfo: MissionInfoDto
)