package com.pixelized.ordiscord.network.dto.worldstate

import com.google.gson.annotations.SerializedName

data class MissionInfo(
        @SerializedName("missionType")
        val missionType : String,
        @SerializedName("faction")
        val faction : String,
        @SerializedName("minEnemyLevel")
        val minEnemyLevel: String,
        @SerializedName("maxEnemyLevel")
        val maxEnemyLevel: String,
        @SerializedName("missionReward")
        val missionReward: MissionReward
)