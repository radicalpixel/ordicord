package com.pixelized.ordiscord.test

import com.pixelized.ordiscord.engine.cmd.CommandStore
import com.pixelized.ordiscord.engine.cmd.model.CommandPattern
import com.pixelized.ordiscord.engine.cmd.model.OptionPattern
import com.pixelized.ordiscord.engine.cmd.CommandStore.ParseException
import org.junit.Assert
import org.junit.Test

class CommandParseJUnitTest {

    @Test
    fun test() {
        val commandStore = object : CommandStore() {
            override val dictionary: List<CommandPattern>
                get() = arrayListOf(
                        CommandPattern(id = "cmd_refresh", keyword = "refresh"),
                        CommandPattern(id = "cmd_item", keyword = "item", options = arrayListOf(
                                OptionPattern("opt_list", "list item", "-l", "--list"),
                                OptionPattern("opt_add", "add item", "-a", "--add", false, 2)
                        )),
                        CommandPattern(id = "cmd_user", keyword = "user", options = arrayListOf(
                                OptionPattern("opt_user", "user", "-u", "--user", true, 1),
                                OptionPattern("opt_item", "item list", "-i", "--item"),
                                OptionPattern("opt_friend", "friend list", "-f", "--friend")
                        ))
                )
        }

        try {
            commandStore.parse("refresh").apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("cmd_refresh", id)
            }
        } catch (e: ParseException) {
            Assert.fail(e.message)
        }

        try {
            commandStore.parse("item").apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("cmd_item", id)
            }
        } catch (e: ParseException) {
            Assert.fail(e.message)
        }

        try {
            commandStore.parse("item -l").apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("cmd_item", id)
                options?.get(0).apply {
                    Assert.assertNotNull(this)
                    Assert.assertNotNull("opt_list", id)
                }
            }
        } catch (e: ParseException) {
            Assert.fail(e.message)
        }

        try {
            commandStore.parse("item --list").apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("cmd_item", id)
                options?.get(0).apply {
                    Assert.assertNotNull(this)
                    Assert.assertNotNull("opt_list", id)
                }
            }
        } catch (e: ParseException) {
            Assert.fail(e.message)
        }

        try {
            commandStore.parse("item -a name path").apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("cmd_item", id)
                options?.get(0).apply {
                    Assert.assertNotNull(this)
                    Assert.assertEquals("opt_add", this?.id)
                    Assert.assertEquals(2, this?.args?.size)
                    Assert.assertEquals("name", this?.args?.get(0))
                    Assert.assertEquals("path", this?.args?.get(1))
                }
            }
        } catch (e: ParseException) {
            Assert.fail(e.message)
        }

        try {
            commandStore.parse("item --add name path").apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("cmd_item", id)
                options?.get(0).apply {
                    Assert.assertNotNull(this)
                    Assert.assertEquals("opt_add", this?.id)
                    Assert.assertEquals(2, this?.args?.size)
                    Assert.assertEquals("name", this?.args?.get(0))
                    Assert.assertEquals("path", this?.args?.get(1))
                }
            }
        } catch (e: ParseException) {
            Assert.fail(e.message)
        }

        try {
            commandStore.parse("user -u <@me>").apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("cmd_user", id)
                options?.get(0).apply {
                    Assert.assertNotNull(this)
                    Assert.assertEquals("opt_user", this?.id)
                    Assert.assertEquals(1, this?.args?.size)
                    Assert.assertEquals("<@me>", this?.args?.get(0))
                }
            }
        } catch (e: ParseException) {
            Assert.fail(e.message)
        }

        try {
            commandStore.parse("user -u <@me> -i").apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("cmd_user", id)
                options?.get(0).apply {
                    Assert.assertNotNull(this)
                    Assert.assertEquals("opt_user", this?.id)
                    Assert.assertEquals(1, this?.args?.size)
                    Assert.assertEquals("<@me>", this?.args?.get(0))
                }
                options?.get(1).apply {
                    Assert.assertNotNull(this)
                    Assert.assertEquals("opt_item", this?.id)
                }
            }
        } catch (e: ParseException) {
            Assert.fail(e.message)
        }

        try {
            commandStore.parse("user -u <@me> -f").apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("cmd_user", id)
                options?.get(0).apply {
                    Assert.assertNotNull(this)
                    Assert.assertEquals("opt_user", this?.id)
                    Assert.assertEquals(1, this?.args?.size)
                    Assert.assertEquals("<@me>", this?.args?.get(0))
                }
                options?.get(1).apply {
                    Assert.assertNotNull(this)
                    Assert.assertEquals("opt_friend", this?.id)
                }
            }
        } catch (e: ParseException) {
            Assert.fail(e.message)
        }

        try {
            commandStore.parse("user -u <@me> -i -f").apply {
                Assert.assertNotNull(this)
                Assert.assertEquals("cmd_user", id)
                options?.get(0).apply {
                    Assert.assertNotNull(this)
                    Assert.assertEquals("opt_user", this?.id)
                    Assert.assertEquals(1, this?.args?.size)
                    Assert.assertEquals("<@me>", this?.args?.get(0))
                }
                options?.get(1).apply {
                    Assert.assertNotNull(this)
                    Assert.assertEquals("opt_item", this?.id)
                }
                options?.get(2).apply {
                    Assert.assertNotNull(this)
                    Assert.assertEquals("opt_friend", this?.id)
                }
            }
        } catch (e: ParseException) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun testMissingKeyword() {
        val commandStore = object : CommandStore() {
            override val dictionary: List<CommandPattern>
                get() = arrayListOf(
                        CommandPattern(id = "refresh", keyword = "refresh")
                )
        }

        try {
            "<azd51azd6azd>".apply {
                commandStore.parse(this)
                Assert.fail("commandStore.parse(\"$this\") should rise an exception !")
            }
        } catch (e: ParseException) {
            Assert.assertTrue(e is CommandStore.ParseException.MissingKeyword)
        }

        try {
            "item -a name path".apply {
                commandStore.parse(this)
                Assert.fail("commandStore.parse(\"$this\") should rise an exception !")
            }
        } catch (e: ParseException) {
            Assert.assertTrue(e is CommandStore.ParseException.MissingKeyword)
        }

        try {
            "".apply {
                commandStore.parse(this)
                Assert.fail("commandStore.parse(\"$this\") should rise an exception !")
            }
        } catch (e: ParseException) {
            Assert.assertTrue(e is CommandStore.ParseException.MissingKeyword)
        }
    }

    @Test
    fun testUnexpectedOption() {
        val commandStore = object : CommandStore() {
            override val dictionary: List<CommandPattern>
                get() = arrayListOf(
                        CommandPattern(id = "refresh", keyword = "refresh"),
                        CommandPattern(id = "item", keyword = "item", options = arrayListOf(
                                OptionPattern("list", "list item", "-l", "--list")
                        ))
                )
        }

        try {
            "refresh -f".apply {
                commandStore.parse(this)
                Assert.fail("commandStore.parse(\"$this\") should rise an exception !")
            }
        } catch (e: ParseException) {
            Assert.assertTrue(e is CommandStore.ParseException.UnexpectedOption)
        }

        try {
            "item -f".apply {
                commandStore.parse(this)
                Assert.fail("commandStore.parse(\"$this\") should rise an exception !")
            }
        } catch (e: ParseException) {
            Assert.assertTrue(e is CommandStore.ParseException.UnexpectedOption)
        }
    }

    @Test
    fun testMissingMandatoryOption() {
        val commandStore = object : CommandStore() {
            override val dictionary: List<CommandPattern>
                get() = arrayListOf(
                        CommandPattern(id = "user", keyword = "user", options = arrayListOf(
                                OptionPattern("user", "user", "-u", "--user", true, 1),
                                OptionPattern("item", "item list", "-i", "--item"),
                                OptionPattern("friend", "friend list", "-f", "--friend")
                        ))
                )
        }

        try {
            "user -i".apply {
                commandStore.parse(this)
                Assert.fail("commandStore.parse(\"$this\") should rise an exception !")
            }
        } catch (e: ParseException) {
            Assert.assertTrue(e is CommandStore.ParseException.MissingMandatoryOption)
        }

        try {
            "user -f".apply {
                commandStore.parse(this)
                Assert.fail("commandStore.parse(\"$this\") should rise an exception !")
            }
        } catch (e: ParseException) {
            Assert.assertTrue(e is CommandStore.ParseException.MissingMandatoryOption)
        }
    }

    @Test
    fun testMissingMandatoryArgument() {
        val commandStore = object : CommandStore() {
            override val dictionary: List<CommandPattern>
                get() = arrayListOf(
                        CommandPattern(id = "item", keyword = "item", options = arrayListOf(
                                OptionPattern("list", "list item", "-l", "--list"),
                                OptionPattern("add", "add item", "-a", "--add", false, 2)
                        ))
                )
        }

        try {
            "item -a name -l".apply {
                commandStore.parse(this)
                Assert.fail("commandStore.parse(\"$this\") should rise an exception !")
            }
        } catch (e: ParseException) {
            Assert.assertTrue(e is CommandStore.ParseException.MissingMandatoryArgument)
        }

        try {
            "item -a name".apply {
                commandStore.parse(this)
                Assert.fail("commandStore.parse(\"$this\") should rise an exception !")
            }
        } catch (e: ParseException) {
            Assert.assertTrue(e is CommandStore.ParseException.MissingMandatoryArgument)
        }

        try {
            "item -a".apply {
                commandStore.parse(this)
                Assert.fail("commandStore.parse(\"$this\") should rise an exception !")
            }
        } catch (e: ParseException) {
            Assert.assertTrue(e is CommandStore.ParseException.MissingMandatoryArgument)
        }
    }
}