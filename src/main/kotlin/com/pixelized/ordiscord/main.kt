package com.pixelized.ordiscord

import com.pixelized.ordiscord.engine.config.Config
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import javax.security.auth.login.LoginException

private const val ARGS_TOKEN = 0

fun main(args: Array<String>) {
    val token = args[ARGS_TOKEN] as String?

    try {
        // todo config parser
        val config = Config(
                channel = "warframe"
        )

        JDABuilder(AccountType.BOT)
                .setToken(token)
                .build()
                .apply {
                    awaitReady()
                    addEventListener(Ordiscord(this, config))
                }

    } catch (e: InterruptedException) {
        e.printStackTrace()
    } catch (e: LoginException) {
        e.printStackTrace()
    }
}

