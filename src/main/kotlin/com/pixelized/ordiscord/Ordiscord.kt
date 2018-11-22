package com.pixelized.ordiscord

import com.pixelized.ordiscord.engine.bot.DiscordBot
import com.pixelized.ordiscord.engine.cmd.CommandStore
import com.pixelized.ordiscord.engine.cmd.model.Command
import com.pixelized.ordiscord.engine.cmd.model.CommandPattern
import com.pixelized.ordiscord.engine.config.Config
import com.pixelized.ordiscord.network.store.WarframeStore
import com.pixelized.ordiscord.util.write
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.MessageChannel
import java.awt.Color

class Ordiscord(jda: JDA, config: Config) : DiscordBot(jda, config) {
    private val store = WarframeStore(config)
    override val commands = object : CommandStore() {
        override val dictionary: List<CommandPattern>
            get() = arrayListOf(
                    CommandPattern(CMD_REFRESH, "refresh"),
                    CommandPattern(CMD_ALERT, "alert"),
                    CommandPattern(CMD_TEST, "test")
            )
    }

    override fun onCommand(channel: MessageChannel, command: Command) {
        when (command.id) {
            CMD_REFRESH -> {
                store.refreshWorldState()
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
            CMD_TEST -> {
                store.worldState.value?.alerts?.forEach { alert ->
                    val delta = alert.expiry.date.long - System.currentTimeMillis()
                    val seconds = (delta / 1000) % 60
                    val minutes = (delta / (1000 * 60) % 60)
                    val hours = (delta / (1000 * 60 * 60) % 24)
                    val builder = EmbedBuilder()
                            .setThumbnail("http://content.warframe.com/MobileExport${alert.missionInfo.missionReward.items?.get(0) ?: ""}.png")
                            .addField("Alert", "${alert.missionInfo.missionType} - ${alert.missionInfo.faction}", false)
                            .addField("Credit", "${alert.missionInfo.missionReward.credits}", true)
                            .addField("Until", "$hours:$minutes:$seconds", true)
                            .setColor(Color.RED)
                    if (alert.missionInfo.missionReward.items?.size ?: 0 > 0) {
                        builder.addField("Item", alert.missionInfo.missionReward.items?.joinToString(separator = ", ") { it }, true)
                    }
                    if (alert.missionInfo.missionReward.countedItems?.size ?: 0 > 0) {
                        builder.addField("Item", alert.missionInfo.missionReward.countedItems?.joinToString(separator = ", ") { it.itemType }, true)
                    }
                    channel.sendMessage(builder.build()).queue()
                }
            }
        }
    }

    override fun onCommandError(channel: MessageChannel, exception: CommandStore.ParseException) {

    }

    companion object {
        const val CMD_REFRESH = "cmd_refresh"
        const val CMD_ALERT = "cmd_alert"
        const val CMD_TEST = "cmd_test"
    }
}