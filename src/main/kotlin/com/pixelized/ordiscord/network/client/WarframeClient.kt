package com.pixelized.ordiscord.network.client

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WarframeClient {
    private val retrofit = Retrofit.Builder()
            .baseUrl("http://content.warframe.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val warframeApi: WarframeAPI = retrofit.create(WarframeAPI::class.java)
}