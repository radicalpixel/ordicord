package com.pixelized.ordiscord.util

import net.dv8tion.jda.core.entities.MessageChannel

fun MessageChannel.write(message: String) = sendMessage(message)?.queue()