package com.pixelized.ordiscord

import com.pixelized.ordiscord.engine.DiscordBot
import com.pixelized.ordiscord.engine.config.Config
import com.pixelized.ordiscord.network.store.WarframeStore
import net.dv8tion.jda.core.JDA

class Ordiscord(jda: JDA, config: Config) : DiscordBot(jda, config) {
    private val store = WarframeStore()
}