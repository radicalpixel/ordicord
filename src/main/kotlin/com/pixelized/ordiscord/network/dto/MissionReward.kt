package com.pixelized.ordiscord.network.dto

import com.google.gson.annotations.SerializedName

data class MissionReward(
        @SerializedName("credits")
        val credits: Int,
        @SerializedName("items")
        val items: List<String>? = null,
        @SerializedName("countedItems")
        val countedItems: List<CountedItems>? = null
)