package com.pixelized.ordiscord.bot

import com.pixelized.ordiscord.store.CommandStore
import com.pixelized.ordiscord.model.command.Command
import com.pixelized.ordiscord.model.config.Config
import com.pixelized.ordiscord.util.Log
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.SelfUser
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener

abstract class DiscordBot(jda: JDA, config: Config) : EventListener {
    private var channel: MessageChannel? = jda.textChannels.find { it.name == config.channel }
    val bot: SelfUser? = jda.selfUser
    abstract val commands: CommandStore

    override fun onEvent(event: Event?) {
        Log.d(this, "Ordiscord::onEvent((" + event?.javaClass?.simpleName + ") event) ->")
        when (event) {
            is MessageReceivedEvent -> {
                if (event.message.mentionedUsers.contains(bot)) {
                    dispatchCommand(event.channel, event.message.contentRaw)
                }
            }
            is PrivateMessageReceivedEvent -> {
                event.message.contentRaw?.apply {
                    dispatchCommand(event.channel, event.message.contentRaw)
                }
            }
        }
    }

    private fun dispatchCommand(channel: MessageChannel, message: String) {
        try {
            onCommand(channel, commands.parse(message))
        } catch (e: CommandStore.ParseException) {
            onCommandError(channel, e)
        }
    }

    abstract fun onCommand(channel: MessageChannel, command: Command)

    abstract fun onCommandError(channel: MessageChannel, exception: CommandStore.ParseException)
}