package com.pixelized.ordiscord.network.dto

import com.google.gson.annotations.SerializedName

data class ItemDto(
        @SerializedName("uniqueName") // "/Lotus/Types/Items/MiscItems/OrokinReactor",
        val id: String,
        @SerializedName("name")       // "Orokin Reactor",
        val name: String,
        @SerializedName("imageName")  // "orokin-reactor.png",
        val image: String?,
        @SerializedName("components")
        val component: List<ItemDto>?
)