package com.pixelized.ordiscord.util

object Log {
    private const val LOGGER_LENGTH = 10

    fun d(logger: Any, message: String) = log(logger, "D", message)

    fun e(logger: Any, message: String, throwable: Throwable? = null) = log(logger, "E", message, throwable)

    private fun log(logger: Any, level: String, message: String, throwable: Throwable? = null) {
        System.out.println("${logger.javaClass.simpleName.padStart(LOGGER_LENGTH).chunked(LOGGER_LENGTH)[0]} - $level - $message")
        throwable?.printStackTrace()
    }
}