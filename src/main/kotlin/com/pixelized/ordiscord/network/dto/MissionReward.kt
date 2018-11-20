package com.pixelized.ordiscord.network.dto

import com.google.gson.annotations.SerializedName

data class MissionReward(
        @SerializedName("credits")
        val credits: Int,
        @SerializedName("countedItems")
        val countedItems: List<CountedItems>
)