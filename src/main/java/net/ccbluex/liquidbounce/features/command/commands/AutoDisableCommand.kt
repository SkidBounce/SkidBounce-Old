/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce.moduleManager
import net.ccbluex.liquidbounce.features.command.Command

/**
 * @see net.ccbluex.liquidbounce.features.module.AutoDisable
 * @see net.ccbluex.liquidbounce.features.module.modules.client.AutoDisable
 * @see net.ccbluex.liquidbounce.file.configs.ModulesConfig
 */
object AutoDisableCommand : Command("autodisable", "ad") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 2) {
            val module = moduleManager[args[1]] ?: run {
                chat("Module §a§l" + args[1] + "§3 not found.")
                return
            }

            if (args[2].lowercase() in listOf("world", "death", "flag", "none")) {
                if (args[2].lowercase() == "none") {
                    module.AutoDisable.disable()
                    chat("Disabled AutoDisable for §a§l${module.getName()}§3.")
                    return
                }

                var enabled = false

                if (args.size > 3) {
                    enabled = args[3].toBoolean()
                    when (args[2].lowercase()) {
                        "flag" -> module.AutoDisable.flag = enabled
                        "death" -> module.AutoDisable.death = enabled
                        "world" -> module.AutoDisable.world = enabled
                    }
                } else {
                    when (args[2].lowercase()) {
                        "flag" -> {
                            module.AutoDisable.flag = !module.AutoDisable.flag
                            enabled = module.AutoDisable.flag
                        }
                        "death" -> {
                            module.AutoDisable.death = !module.AutoDisable.death
                            enabled = module.AutoDisable.death
                        }
                        "world" -> {
                            module.AutoDisable.world = !module.AutoDisable.world
                            enabled = module.AutoDisable.world
                        }
                    }
                }

                chat("${if (enabled) "Enabled" else "Disabled"} AutoDisable §a§l${args[2].uppercase()} §3for §a§l${module.getName()}§3.")
                playEdit()
                return
            }
        }

        chatSyntax(arrayOf("<module> <world|death|flag> <enabled>", "<module> none"))
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> moduleManager.modules
                    .map { it.name }
                    .filter { it.startsWith(args[0], true) }
                    .toList()
            2 -> listOf("world", "death", "flag", "none").filter { it.startsWith(args[1], true) }.toList()
            3 -> if (args[2] != "none") listOf("true", "false") else emptyList()
            else -> emptyList()
        }
    }
}
