package com.pixelized.ordiscord

import com.pixelized.ordiscord.engine.bot.DiscordBot
import com.pixelized.ordiscord.engine.cmd.CommandStore
import com.pixelized.ordiscord.engine.cmd.model.Command
import com.pixelized.ordiscord.engine.cmd.model.CommandPattern
import com.pixelized.ordiscord.engine.config.Config
import com.pixelized.ordiscord.network.store.WarframeStore
import com.pixelized.ordiscord.util.write
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.MessageChannel

class Ordiscord(jda: JDA, config: Config) : DiscordBot(jda, config) {
    private val store = WarframeStore()
    override val commands = object : CommandStore() {
        override val dictionary: List<CommandPattern>
            get() = arrayListOf(
                    CommandPattern(CMD_REFRESH, "refresh"),
                    CommandPattern(CMD_ALERT, "alert")
            )
    }

    override fun onCommand(channel: MessageChannel, command: Command) {
        when (command.id) {
            CMD_REFRESH -> {
                store.refresh()
            }
            CMD_ALERT -> {
                channel.write(
                        store.worldState.value?.alerts?.joinToString(separator = "\n") { alert ->
                            "${alert.missionInfo.missionType} - ${alert.missionInfo.faction} - ${alert.missionInfo.missionReward.credits} credits"
                                    .plus(if (alert.missionInfo.missionReward.items?.size ?: 0 > 0) "\n" + alert.missionInfo.missionReward.items?.joinToString(separator = ", ") { it } else "")
                                    .plus(if (alert.missionInfo.missionReward.countedItems?.size ?: 0 > 0) "\n" + alert.missionInfo.missionReward.countedItems?.joinToString(separator = ", ") { it.itemType } else "")
                        } ?: ""
                )
            }
        }
    }

    override fun onCommandError(channel: MessageChannel, exception: CommandStore.ParseException) {

    }

    companion object {
        const val CMD_REFRESH = "cmd_refresh"
        const val CMD_ALERT = "cmd_alert"
    }
}