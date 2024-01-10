/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.LiquidBounce.moduleManager
import net.ccbluex.liquidbounce.api.ClientApi
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.utils.ClientUtils.displayChatMessage
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import net.ccbluex.liquidbounce.utils.misc.StringUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils.translateAlternateColorCodes
import net.ccbluex.liquidbounce.value.*
import org.lwjgl.input.Keyboard
import kotlin.reflect.KMutableProperty0

/**
 * Utility class for handling settings and scripts in LiquidBounce.
 */
object SettingsUtils {

    /**
     * Execute settings script.
     * @param script The script to apply.
     */
    fun applyScript(script: String) {
        script.lines().forEachIndexed { index, s ->
            if (s.isEmpty() || s.startsWith('#')) {
                return@forEachIndexed
            }

            val args = s.split(" ").toTypedArray()

            if (args.size <= 1) {
                displayChatMessage("§cSyntax error at line '$index' in setting script.\n§8§lLine: §7$s")
                return@forEachIndexed
            }

            when (args[0]) {
                "chat" -> displayChatMessage(
                    "§e${
                        translateAlternateColorCodes(
                            StringUtils.toCompleteString(
                                args,
                                1
                            )
                        )
                    }"
                )

                "unchat" -> displayChatMessage(
                    translateAlternateColorCodes(
                        StringUtils.toCompleteString(
                            args,
                            1
                        )
                    )
                )

                "load" -> {
                    val url = StringUtils.toCompleteString(args, 1)
                    runCatching {
                        val settings = if (url.startsWith("http")) {
                            val (text, code) = HttpUtils.get(url)

                            if (code != 200) {
                                error(text)
                            }

                            text
                        } else {
                            ClientApi.requestSettingsScript(url)
                        }

                        applyScript(settings)
                    }.onSuccess {
                        displayChatMessage("§7[§3§lAutoSettings§7] §7Loaded settings §a§l$url§7.")
                    }.onFailure {
                        displayChatMessage("§7Failed to load settings §c§l$url§7.")
                    }
                }

                else -> {
                    if (args.size < 3) {
                        displayChatMessage("§cSyntax error at line '$index' in setting script.\n§8§lLine: §7$s")
                        return@forEachIndexed
                    }

                    val moduleName = args[0]
                    val valueName = args[1]
                    val value = args[2]
                    val module = moduleManager[moduleName]

                    if (module == null) {
                        displayChatMessage("§cModule §a$moduleName§c does not exist!")
                        return@forEachIndexed
                    }

                    when (valueName) {
                        "toggle" -> setToggle(module, value)
                        "bind" -> setBind(module, value)
                        else -> setValue(module, valueName, value, args)
                    }
                }
            }
        }

        FileManager.saveConfig(FileManager.valuesConfig)
    }
    private fun setToggle(module: Module, value: String) {
        module.state = value.equals("true", ignoreCase = true)
    }
    private fun setBind(module: Module, value: String) {
        module.keyBind = Keyboard.getKeyIndex(value)
    }

    // Utility functions for setting values
    private fun setValue(module: Module, valueName: String, value: String, args: Array<String>) {
        val moduleValue = module[valueName]

        if (moduleValue == null) {
            displayChatMessage("§cValue §a§l$valueName§c wasn't found in module §a§l${module.getName()}§c.")
            return
        }

        try {
            when (moduleValue) {
                is BoolValue -> moduleValue.changeValue(value.toBoolean())
                is FloatValue -> moduleValue.changeValue(value.toFloat())
                is IntegerValue -> moduleValue.changeValue(value.toInt())
                is TextValue -> moduleValue.changeValue(StringUtils.toCompleteString(args, 2))
                is ListValue -> moduleValue.changeValue(value)
            }
        } catch (e: Exception) {
            displayChatMessage("§a§l${e.javaClass.name}§7(${e.message}) §cAn Exception occurred while setting §a§l$value§c to §a§l${moduleValue.name}§c in §a§l${module.getName()}§c.")
        }
    }

    /**
     * Generate settings script.
     * @return The generated script.
     */
    fun generateScript(): String {
        return moduleManager.modules
            .filter { !it.subjective }
            .joinToString("\n") { module ->
                buildString {
                    val vals = module.values.filter { !it.subjective }
                    if (vals.isNotEmpty()) {
                        vals.joinTo(this, separator = "\n") { "${module.name} ${it.name} ${it.get()}" }
                        appendLine()
                    }
                    appendLine("${module.name} toggle ${module.state}")
                    appendLine("${module.name} bind ${Keyboard.getKeyName(module.keyBind)}")
                }
            }.lines().filter { it.isNotBlank() }.joinToString("\n")
    }
}
