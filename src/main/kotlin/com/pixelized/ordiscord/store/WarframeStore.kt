package com.pixelized.ordiscord.store

import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.pixelized.ordiscord.model.config.Config
import com.pixelized.ordiscord.model.item.Item
import com.pixelized.ordiscord.model.world.Alert
import com.pixelized.ordiscord.network.client.WarframeClient
import com.pixelized.ordiscord.network.dto.item.ItemDto
import com.pixelized.ordiscord.network.dto.worldstate.WorldStateDto
import com.pixelized.ordiscord.util.Observable
import java.io.File
import java.io.FileReader

class WarframeStore(private val config: Config) {
    private val client = WarframeClient()
    private val gson = GsonBuilder().create()

    val alerts = Observable<List<Alert>>(listOf())
    val items = Observable<List<Item>>(listOf())

    fun refreshWorldState() {
        // refresh the data cache
        val response = client.warframeApi.worldState().execute()
        // update model
        if (response.isSuccessful) {
            val worldState = response.body()
            alerts.value = worldState?.let {
                it.alerts.map {
                    Alert(activation = it.activation.date.long,
                            expiry = it.expiry.date.long,
                            missionType = it.missionInfo.missionType,
                            faction = it.missionInfo.faction,
                            minEnemyLevel = it.missionInfo.minEnemyLevel,
                            maxEnemyLevel = it.missionInfo.maxEnemyLevel,
                            credits = it.missionInfo.missionReward.credits,
                            reward = mutableListOf<String>()
                                    .plus(it.missionInfo.missionReward.items?.map { it.name() } ?: listOf())
                                    .plus(it.missionInfo.missionReward.countedItems?.map { it.itemType.name() }
                                            ?: listOf()))
                }
            } ?: listOf()
        } else {
            // todo : error management.
        }
    }

    fun refreshItems() {
        // todo : update the data cache, download a new data.json
        // get the data file and read
        val reader = JsonReader(FileReader(File(config.itemsPath)))
        // parse the file
        val itemsDto = gson.fromJson<Array<ItemDto>>(reader, Array<ItemDto>::class.java)
        // update the value of the store.
        items.value = itemsDto
                .filter { it.component != null }
                .flatMap { it.component!! }
                .plus(itemsDto)
                .map { Item(it.id, it.name, it.image) }
    }

    private fun String.name() = items.value.find { it.id == this }?.name ?: this
}