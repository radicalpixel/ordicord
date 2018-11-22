package com.pixelized.ordiscord.network.dto.worldstate

import com.google.gson.annotations.SerializedName

data class MissionRewardDto(
        @SerializedName("credits")
        val credits: Int,
        @SerializedName("items")
        val items: List<String>? = null,
        @SerializedName("countedItems")
        val countedItems: List<CountedItemDto>? = null
)