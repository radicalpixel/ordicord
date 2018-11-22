package com.pixelized.ordiscord.bot

import com.pixelized.ordiscord.store.CommandStore
import com.pixelized.ordiscord.model.command.Command
import com.pixelized.ordiscord.model.command.CommandPattern
import com.pixelized.ordiscord.model.command.Option
import com.pixelized.ordiscord.model.command.OptionPattern
import com.pixelized.ordiscord.model.config.Config
import com.pixelized.ordiscord.model.item.Item
import com.pixelized.ordiscord.store.WarframeStore
import com.pixelized.ordiscord.util.haveOption
import com.pixelized.ordiscord.util.imageFrom
import com.pixelized.ordiscord.util.nameFrom
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
                    CommandPattern(CMD_REFRESH, "refresh", listOf(
                            OptionPattern(OPT_HELP, short = "-h", long = "--long",
                                    description = "Show the command help"),
                            OptionPattern(OPT_WORLD, short = "-w", long = "--world",
                                    description = "This will force a refresh of Warframe world state, alert, invasion, sortie will be updated."),
                            OptionPattern(OPT_ITEM, short = "-i", long = "--item",
                                    description = "This will force a refresh of Warframe item data, id/name/image of items will be updated.")
                    )),
                    CommandPattern(CMD_ALERT, "alert", listOf(
                            OptionPattern(OPT_HELP, short = "-h", long = "--long",
                                    description = "Show the command help"),
                            OptionPattern(OPT_TEXT, short = "-t", long = "--text",
                                    description = "display a simplified text version of this command.")
                    )),
                    CommandPattern(CMD_ITEM, "item", listOf(
                            OptionPattern(OPT_HELP, short = "-h", long = "--long",
                                    description = "Show the command help"),
                            OptionPattern(OPT_NAME, short = "-n", long = "--name", args = 1,
                                    description = "search an item by is name"),
                            OptionPattern(OPT_ID, short = "-d", long = "--id", args = 1,
                                    description = "search an item by is id"),
                            OptionPattern(OPT_CONTAIN, short = "-c", long = "--contain",
                                    description = "the research will check if the search criteria is contain in the name/id of the item instead of equal."),
                            OptionPattern(OPT_IGNORE_CASE, short = "-i", long = "--ignore",
                                    description = "the research will ignore the case.")
                    )),
                    CommandPattern(CMD_TEST, "test")
            )
    }

    override fun onCommand(channel: MessageChannel, command: Command) {
        when (command.id) {
            CMD_REFRESH -> {
                if (command haveOption OPT_HELP && command.options?.size == 1) {
                    channel.write(commands.buildHelpMessage(command))
                } else {
                    if (command haveOption OPT_ITEM) {
                        store.refreshItems()
                    }
                    if (command haveOption OPT_WORLD) {
                        store.refreshWorldState()
                    }
                }
            }

            CMD_ALERT -> {
                if (command haveOption OPT_HELP && command.options?.size == 1) {
                    channel.write(commands.buildHelpMessage(command))
                } else {
                    if (command haveOption OPT_TEXT) {
                        channel.write(store.alerts.value.joinToString(separator = "\n") { alert ->
                            "${alert.missionType} - ${alert.faction} - ${alert.credits} credits" +
                                    alert.reward.joinToString(prefix = "\n", separator = ", ") {
                                        store.items.value.find { item -> item.id == it }?.name ?: it
                                    }
                        })
                    } else {
                        store.alerts.value.forEach { alert ->
                            // remaining time
                            val delta = alert.expiry - System.currentTimeMillis()
                            val seconds = (delta / 1000) % 60
                            val minutes = (delta / (1000 * 60) % 60)
                            val hours = (delta / (1000 * 60 * 60) % 24)
                            // item
                            val builder = EmbedBuilder()
                                    .addField("Alert", "${alert.missionType} - ${alert.faction}", false)
                                    .addField("Credit", "${alert.credits}", true)
                                    .addField("Until", "$hours:$minutes:$seconds", true)
                                    .setColor(Color.RED)
                            if (alert.reward.isNotEmpty()) {
                                builder.addField("Item", alert.reward.joinToString(prefix = "\n", separator = ", ") { it nameFrom store }, true)
                                builder.setThumbnail("https://cdn.warframestat.us/img/${alert.reward[0] imageFrom store}")
                            }
                            channel.sendMessage(builder.build()).queue()
                        }
                    }
                }
            }

            CMD_ITEM -> {
                if (command haveOption OPT_HELP && command.options?.size == 1) {
                    channel.write(commands.buildHelpMessage(command))
                } else {
                    val ignoreCaseOption = command haveOption OPT_IGNORE_CASE
                    val containOption = command haveOption OPT_CONTAIN
                    val items = if (command haveOption OPT_NAME) {
                        val name = command.options?.find { it.id == OPT_NAME }?.args?.getOrNull(0) ?: ""
                        if (containOption) {
                            store.items.value.filter { it.name.contains(name, ignoreCaseOption) }
                        } else {
                            store.items.value.filter { it.name.equals(name, ignoreCaseOption) }
                        }
                    } else if (command haveOption OPT_ID) {
                        val id = command.options?.find { it.id == OPT_ID }?.args?.getOrNull(0) ?: ""
                        if (containOption) {
                            store.items.value.filter { it.id.contains(id, ignoreCaseOption) }
                        } else {
                            store.items.value.filter { it.id.equals(id, ignoreCaseOption) }
                        }
                    } else {
                        listOf()
                    }
                    if (items.isNotEmpty()) {
                        channel.write(items.joinToString(separator = "\n", limit = 10) { it.toString() })
                    } else {
                        channel.write("No item fit your request Tenno")
                    }
                }
            }

            CMD_TEST -> {

            }
        }
    }

    override fun onCommandError(channel: MessageChannel, exception: CommandStore.ParseException) {
        channel.write("Tenno, I have encounter an error !\n${exception.message}")
    }

    companion object {
        const val CMD_REFRESH = "cmd_refresh"
        const val CMD_ALERT = "cmd_alert"
        const val CMD_ITEM = "cmd_item"
        const val CMD_TEST = "cmd_test"
        const val OPT_HELP = "opt_help"
        const val OPT_ITEM = "opt_item"
        const val OPT_WORLD = "opt_world"
        const val OPT_TEXT = "opt_text"
        const val OPT_CONTAIN = "opt_contain"
        const val OPT_IGNORE_CASE = "opt_ignore_case"
        const val OPT_ID = "opt_id"
        const val OPT_NAME = "opt_name"
    }
}