package com.pixelized.ordiscord.util

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.User

fun JDA.getUserByDiscriminator(user: String, ignoreCase: Boolean = false): List<User> =
        users.filter { user.contains(it.name, ignoreCase) && user.contains(it.discriminator, ignoreCase) }
