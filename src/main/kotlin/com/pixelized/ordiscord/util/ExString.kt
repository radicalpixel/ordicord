package com.pixelized.ordiscord.util

import com.pixelized.ordiscord.store.WarframeStore

infix fun String.nameFrom(store: WarframeStore) = store.items.value.find { it.id == this }?.name ?: this.split("/").last()

infix fun String.imageFrom(store: WarframeStore) = store.items.value.find { it.id == this }?.image ?: this