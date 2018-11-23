package com.pixelized.ordiscord.store

import com.pixelized.ordiscord.model.command.Command
import com.pixelized.ordiscord.model.command.CommandPattern
import com.pixelized.ordiscord.model.command.Option
import com.pixelized.ordiscord.model.command.OptionPattern

import java.util.ArrayList
import java.util.regex.Pattern

abstract class CommandStore {
    // regex
    private val commandRegex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'")
    private val optionRegex = Pattern.compile("^(-{2}\\w*|-\\w|-[a-zA-Z]{2,})\$")
    private val multipleOptionRegex = Pattern.compile("^-[a-zA-Z]{2,}$")
    private val userRegex = Pattern.compile("^<(.*?)>\$")

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
     * @see ParseException.TooManyArgument
     * @see ParseException.NonConcatenateOption
     */
    @Throws(ParseException::class)
    fun parse(commandLine: String): Command {
        lateinit var command: Command
        var commandPattern: CommandPattern? = null
        var optionPattern: OptionPattern? = null
        for (argument in split(commandLine)) {
            // ignore this argument if it match a user and the command pattern have not been found yet.
            if (commandPattern == null && userRegex.matcher(argument).find()) {
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
                            args = if (commandPattern.args > 0) arrayListOf() else null,
                            options = if (commandPattern.options != null) arrayListOf() else null)
                }
            } else {
                // check if <argument> is an option of not
                if (optionRegex.matcher(argument).find()) {
                    // <argument> is an (or more) option.
                    if (optionPattern != null) {
                        val option = command.options?.last()
                        if (optionPattern.args > option?.args?.size ?: 0) {
                            throw ParseException.MissingMandatoryArgument(optionPattern, commandLine)
                        }
                    }
                    if (multipleOptionRegex.matcher(argument).find()) {
                        // <argument> is a multiple option
                        command.options?.addAll((1 until argument.length)
                                .map {
                                    "-${argument[it]}"
                                }.map { option ->
                                    commandPattern.options?.find { it.short == option }?.apply {
                                        if (args > 0) throw ParseException.NonConcatenateOption(commandLine)
                                    } ?: throw ParseException.UnexpectedOption(commandLine)
                                }.map {
                                    Option(id = it.id, args = if (it.args > 0) arrayListOf() else null)
                                })
                    } else {
                        // <argument> is a single option
                        optionPattern = commandPattern.options?.find { it.short == argument || it.long == argument }
                        if (optionPattern != null) {
                            command.options?.add(Option(id = optionPattern.id,
                                    args = if (optionPattern.args > 0) arrayListOf() else null))
                        } else {
                            throw ParseException.UnexpectedOption(commandLine)
                        }
                    }
                } else {
                    // <argument> is not an option.
                    val option = command.options?.lastOrNull()
                    if (optionPattern == null || commandPattern.args > 0 && optionPattern.args == option?.args?.size ?: 0) {
                        // the argument is a command argument.
                        if (commandPattern.args < (command.args?.size ?: 0) + 1) {
                            throw ParseException.TooManyArgument(commandPattern, commandLine)
                        } else {
                            command.args?.add(argument)
                        }
                    } else {
                        // the argument is an option argument.
                        if (optionPattern.args < (option?.args?.size ?: 0) + 1) {
                            throw ParseException.TooManyArgument(optionPattern, commandLine)
                        } else {
                            option?.args?.add(argument)
                        }
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
        // check if we have all mandatory arguments for the command.
        if (commandPattern != null && command.args?.size ?: 0 < commandPattern.args) {
            throw ParseException.MissingMandatoryArgument(commandPattern, commandLine)
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

        class MissingMandatoryArgument : ParseException {
            constructor(optionPattern: OptionPattern, commandLine: String) : super("\"$commandLine\" :: Missing mandatory argument, at least ${optionPattern.args} arguments needed for option: ${optionPattern.short}")
            constructor(commandPattern: CommandPattern, commandLine: String) : super("\"$commandLine\" :: Missing mandatory argument, at least ${commandPattern.args} arguments needed for command: ${commandPattern.keyword}")
        }

        class TooManyArgument : ParseException {
            constructor(optionPattern: OptionPattern, commandLine: String) : super("\"$commandLine\" :: Too many arguments, no more than ${optionPattern.args} arguments are require for option: ${optionPattern.short}")
            constructor(commandPattern: CommandPattern, commandLine: String) : super("\"$commandLine\" :: Too many arguments, no more than ${commandPattern.args} arguments are require for command: ${commandPattern.keyword}")
        }

        class NonConcatenateOption(commandLine: String)
            : ParseException("\"$commandLine\" :: impossible to use option concatenation on option that require arguments ")
    }
}
