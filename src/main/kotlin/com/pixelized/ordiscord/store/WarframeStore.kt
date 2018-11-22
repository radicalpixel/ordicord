package com.pixelized.ordiscord.store

import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.pixelized.ordiscord.model.config.Config
import com.pixelized.ordiscord.model.item.Item
import com.pixelized.ordiscord.network.client.WarframeClient
import com.pixelized.ordiscord.network.dto.item.ItemDto
import com.pixelized.ordiscord.network.dto.worldstate.WorldState
import com.pixelized.ordiscord.util.Observable
import java.io.File
import java.io.FileReader

class WarframeStore(private val config: Config) {
    private val client = WarframeClient()
    private val gson = GsonBuilder().create()

    val worldState = Observable<WorldState?>(null)
    val items = Observable<List<Item>>(listOf())

    fun refreshWorldState() {
        val response = client.warframeApi.worldState().execute()
        if (response.isSuccessful) {
            response.body()?.let { worldState.value = it }
        } else {
            // todo : error management.
        }
    }

    fun refreshItems() {
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
}