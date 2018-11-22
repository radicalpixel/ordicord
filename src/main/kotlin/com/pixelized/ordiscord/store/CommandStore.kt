package com.pixelized.ordiscord.store

import com.pixelized.ordiscord.model.command.Command
import com.pixelized.ordiscord.model.command.CommandPattern
import com.pixelized.ordiscord.model.command.Option
import com.pixelized.ordiscord.model.command.OptionPattern
import com.pixelized.ordiscord.util.isUser
import java.text.ParseException

import java.util.ArrayList
import java.util.regex.Pattern

abstract class CommandStore {
    // regex
    private val commandRegex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'")
    private val multipleOptionRegex = Pattern.compile("^-{1}[a-zA-Z]{2,}$")

    abstract val dictionary: List<CommandPattern>

    /**
     * This methods will check if any of the CommandPattern in its dictionary fit the commandLine.
     * @param commandLine the command line to parse into a Command
     * @return a non null Command instance.
     * @throws ParseException if the command line does not fit any CommandPattern from the dictionary
     * @see ParseException.MissingKeyword
     * @see ParseException.UnexpectedOption
     * @see ParseException.MissingMandatoryOption
     * @see ParseException.MissingMandatoryArgument
     */
    @Throws(ParseException::class)
    fun parse(commandLine: String): Command {
        lateinit var command: Command
        var commandPattern: CommandPattern? = null
        var optionPattern: OptionPattern? = null
        for (argument in split(commandLine)) {
            // ignore this argument if it match a user and the command pattern have not been found yet.
            if (commandPattern == null && argument.isUser()) {
                continue
            }
            if (commandPattern == null) {
                // find the command pattern
                commandPattern = dictionary.find { it.keyword == argument }
                if (commandPattern == null) {
                    throw ParseException.MissingKeyword(commandLine)
                } else {
                    command = Command(
                            id = commandPattern.id,
                            options = if (commandPattern.options != null) arrayListOf() else null
                    )
                }
            } else {
                // we ether don't have a option pattern yet, or we filled the previous one arguments, find an option or rise an exception.
                if (optionPattern == null || optionPattern.args == command.options?.lastOrNull()?.args?.size ?: 0) {
                    if (multipleOptionRegex.matcher(argument).find()) {
                        (1 until argument.length).forEach { "-${argument[it]}".let { argument ->
                            val optionPattern = commandPattern.options?.find { it.short == argument || it.long == argument }
                            if (optionPattern != null) {
                                if (optionPattern.args == 0) {
                                    command.options?.add(Option(
                                            id = optionPattern.id,
                                            args = if (optionPattern.args > 0) arrayListOf() else null
                                    ))
                                } else {
                                    throw ParseException.UnconcatenableOption(commandLine)
                                }
                            } else {
                                throw ParseException.UnexpectedOption(commandLine)
                            }
                        }}
                    } else {
                        optionPattern = commandPattern.options?.find { it.short == argument || it.long == argument }
                        if (optionPattern == null) {
                            throw ParseException.UnexpectedOption(commandLine)
                        } else {
                            command.options?.add(Option(
                                    id = optionPattern.id,
                                    args = if (optionPattern.args > 0) arrayListOf() else null
                            ))
                        }
                    }
                } else {
                    // the option pattern need at more arguments that we currently have.
                    val otherOptionPattern = commandPattern.options?.find { it.short == argument || it.long == argument }
                    if (otherOptionPattern != null) {
                        throw ParseException.MissingMandatoryArgument(optionPattern, commandLine)
                    } else {
                        command.options?.last()?.args?.add(argument)
                    }
                }
            }
        }
        // check if we have all the argument of the last option
        try {
            if (command.options?.lastOrNull()?.args?.size ?: 0 != optionPattern?.args ?: 0) {
                optionPattern?.apply { throw ParseException.MissingMandatoryArgument(this, commandLine) }
            }
        } catch (e: UninitializedPropertyAccessException) {
            throw ParseException.MissingKeyword(commandLine)
        }
        // check if we have all mandatory options.
        if (commandPattern?.options?.filter { it.mandatory }?.map { pattern -> command.options?.find { it.id == pattern.id } }?.any { it == null } == true) {
            throw ParseException.MissingMandatoryOption(commandPattern, commandLine)
        }
        return command
    }

    /**
     * Help message builder method.
     * @param command the command from which a help message will be built.
     * @return a non null String instance
     */
    fun buildHelpMessage(command: Command) = dictionary.find { it.id == command.id }?.let { pattern ->
        StringBuilder("Usage: ${pattern.keyword} [OPTION]\n").apply {
            if (pattern.options?.size ?: 0 > 0) {
                val spaces = pattern.options?.map {
                    it.short.length + (it.long?.length?.plus(", ".length) ?: 0) + 3
                }?.max() ?: 0
                pattern.options?.forEach {
                    append("${it.short}${if (it.long != null) ", ${it.long}" else ""}".padEnd(spaces))
                    append("${it.description.replace("\n", "\n".padEnd(spaces + 1))}\n")
                }
            }
        }.toString()
    } ?: ""

    /**
     * helper method to split a command line into a array of String.
     * @return a non null instance of a ArrayList of String
     */
    private fun split(commandLine: String): ArrayList<String> {
        val matchList = ArrayList<String>()
        val regexMatcher = commandRegex.matcher(commandLine)
        while (regexMatcher.find()) {
            when {
                regexMatcher.group(1) != null -> matchList.add(regexMatcher.group(1))
                regexMatcher.group(2) != null -> matchList.add(regexMatcher.group(2))
                else -> matchList.add(regexMatcher.group())
            }
        }
        return matchList
    }

    sealed class ParseException(message: String) : RuntimeException(message) {
        class MissingKeyword(commandLine: String)
            : ParseException("\"$commandLine\" :: Missing command keyword.")

        class UnexpectedOption(commandLine: String)
            : ParseException("\"$commandLine\" :: Unexpected option.")

        class MissingMandatoryOption(commandPattern: CommandPattern, commandLine: String)
            : ParseException("\"$commandLine\" :: Missing at least one mandatory option (${commandPattern.options?.filter { it.mandatory }?.joinToString { it.short }})")

        class MissingMandatoryArgument(optionPattern: OptionPattern, commandLine: String)
            : ParseException("\"$commandLine\" :: Missing mandatory argument, at least ${optionPattern.args} arguments needed for option: ${optionPattern.short}")

        class UnconcatenableOption(commandLine: String)
            : ParseException("\"$commandLine\" :: impossible to use option concatenation on option that require arguments ")
    }
}