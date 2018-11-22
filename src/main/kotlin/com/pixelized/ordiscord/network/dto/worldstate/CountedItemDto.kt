package com.pixelized.ordiscord.network.dto.worldstate

import com.google.gson.annotations.SerializedName

data class CountedItemDto (
    @SerializedName("ItemType")
    val itemType: String,
    @SerializedName("ItemCount")
    val itemCount: Int
)