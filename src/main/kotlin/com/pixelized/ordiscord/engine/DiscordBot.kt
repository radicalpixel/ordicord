package com.pixelized.ordiscord.engine

import com.pixelized.ordiscord.engine.config.Config
import com.pixelized.ordiscord.util.Log
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.SelfUser
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener

open class DiscordBot(jda: JDA, config: Config): EventListener {
    private val commands = ArrayList<Command>()
    private var bot: SelfUser? = jda.selfUser
    private var channel: MessageChannel? = jda.textChannels.find { it.name == config.channel }

    override fun onEvent(event: Event?) {
        Log.d(this, "Ordiscord::onEvent((" + event?.javaClass?.simpleName + ") event) ->")

        when (event) {
            is MessageReceivedEvent -> {
                if (event.message.mentionedUsers.contains(bot)) {
                    onMessage(event.channel, event.message.contentRaw)
                }
            }
            is PrivateMessageReceivedEvent -> {
                event.message.contentRaw?.apply {
                    onMessage(event.channel, event.message.contentRaw)
                }
            }
        }
    }

    private fun onMessage(channel: MessageChannel, message: String) {
        Log.d(this, message)
    }
}