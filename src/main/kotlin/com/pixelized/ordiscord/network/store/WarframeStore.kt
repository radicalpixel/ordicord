package com.pixelized.ordiscord.network.store

import com.pixelized.ordiscord.network.client.WarframeClient
import com.pixelized.ordiscord.network.dto.WorldState
import com.pixelized.ordiscord.util.Observable

class WarframeStore {
    private val client = WarframeClient()

    val worldState = Observable<WorldState?>( null )

    fun refresh() {
        val response = client.warframeApi.worldState().execute()
        if (response.isSuccessful) {
            response.body()?.let { worldState.value = it }
        } else {
            // todo : error management.
        }
    }
}