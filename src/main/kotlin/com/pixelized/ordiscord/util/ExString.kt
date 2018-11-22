package com.pixelized.ordiscord.util

import java.util.regex.Pattern

private val userRegex = Pattern.compile("^<(.*?)>\$")

fun String.isUser(): Boolean = userRegex.matcher(this).find()