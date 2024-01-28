/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.command.commands


import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.file.FileManager.settingsDir
import net.ccbluex.liquidbounce.ui.client.hud.HUD.addNotification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils.LOGGER
import net.ccbluex.liquidbounce.utils.SettingsUtils
import java.awt.Desktop
import java.io.File
import java.io.IOException

object ConfigCommand : Command("config", "settings", "c", "cfg") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        val usedAlias = args[0].lowercase().removePrefix(".")
        if (args.size <= 1) {
            chatSyntax("$usedAlias <load/save/list/delete/folder/rename>")
            return
        }
        when (args[1].lowercase()) {
            "load" -> {
                if (args.size <= 2) {
                    chatSyntax("$usedAlias load <name>")
                    return
                }
                val file = File(settingsDir, args[2])
                try {
                    val settings = file.readText()
                    SettingsUtils.applyScript(settings)
                    chat("§6Config §a${args[2]} §6loaded successfully.")
                    addNotification(Notification("Updated Settings"))
                    playEdit()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            "rename" -> {
                if (args.size <= 3) {
                    chatSyntax("$usedAlias rename <oldName> <newName>")
                    return
                }

                val oldFile = File(settingsDir, args[2])
                val newFile = File(settingsDir, args[3])

                if (!oldFile.exists()) {
                    chat("§cConfig §a${args[2]} §cdoes not exist!")
                    return
                }
                if (newFile.exists()) newFile.delete()

                oldFile.renameTo(newFile)
                chat("§6Config §a${args[2]} §6renamed to §a${args[3]} §6successfully.")

            }
            "save" -> {
                if (args.size <= 2) {
                    chatSyntax("$usedAlias save <name>")
                    return
                }

                val file = File(settingsDir, args[2])

                try {
                    if (file.exists()) file.delete()

                    file.createNewFile()
                    val settingsScript = SettingsUtils.generateScript()
                    file.writeText(settingsScript)

                    chat("§6Config §a${args[2]} §6saved successfully.")
                } catch (throwable: Throwable) {
                    chat("§cFailed to create config: §3${throwable.message}")
                    LOGGER.error("Failed to create config.", throwable)
                }
            }

            "delete" -> {
                if (args.size <= 2) {
                    chatSyntax("$usedAlias delete <name>")
                    return
                }

                val file = File(settingsDir, args[2])

                if (!file.exists()) {
                    chat("§cConfig §a${args[2]} §cdoes not exist!")
                    return
                }

                file.delete()

                chat("§6Config §a${args[2]} §6deleted successfully.")
            }

            "list" -> {
                val settings = getLocalSettings() ?: return
                if (settings.isEmpty()) {
                    chat("§6No Configs Found.")
                    return
                }
                chat("§6Configs:")
                for (file in settings)
                    chat("  §a${file.name}")
            }

            "folder" -> Desktop.getDesktop().open(settingsDir)

            else -> chatSyntax("$usedAlias <load/save/list/delete/folder/rename>")
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("delete", "folder", "list", "load", "rename", "save").filter { it.startsWith(args[0], true) }

            2 ->
                when (args[0].lowercase()) {
                    "delete", "load", "rename" -> {
                        val settings = getLocalSettings() ?: return emptyList()

                        settings
                            .map { it.name }
                            .filter { it.startsWith(args[1], true) }
                    }

                    else -> emptyList()
                }

            else -> emptyList()
        }
    }

    private fun getLocalSettings() = settingsDir.listFiles()
}
