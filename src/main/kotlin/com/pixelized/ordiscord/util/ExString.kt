package com.pixelized.ordiscord.util

import com.pixelized.ordiscord.store.WarframeStore
import java.util.regex.Pattern

private val userRegex = Pattern.compile("^<(.*?)>\$")

fun String.isUser(): Boolean = userRegex.matcher(this).find()

infix fun String.nameFrom(store:WarframeStore) = store.items.value.find { it.id == this }?.name ?: this.split("/").last()

infix fun String.imageFrom(store:WarframeStore) = store.items.value.find { it.id == this }?.image ?: this