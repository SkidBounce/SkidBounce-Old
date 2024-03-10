/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.CLIENT
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.utils.ClientUtils.displayClientMessage
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.input.Mouse

object MidClick : Module("MidClick", CLIENT, subjective = true, gameDetecting = false) {
    private var wasDown = false

    @EventTarget
    fun onRender(event: Render2DEvent) {
        if (mc.currentScreen != null)
            return

        if (!wasDown && Mouse.isButtonDown(2)) {
            val entity = mc.objectMouseOver.entityHit

            if (entity is EntityPlayer) {
                val playerName = ColorUtils.stripColor(entity.name)

                if (!FileManager.friendsConfig.isFriend(playerName)) {
                    FileManager.friendsConfig.addFriend(playerName)
                    FileManager.saveConfig(FileManager.friendsConfig)
                    displayClientMessage("§a$playerName§c was added to your friends.")
                } else {
                    FileManager.friendsConfig.removeFriend(playerName)
                    FileManager.saveConfig(FileManager.friendsConfig)
                    displayClientMessage("§a$playerName§c was removed from your friends.")
                }

            } else
                displayClientMessage("§cError: §aYou need to select a player.")
        }
        wasDown = Mouse.isButtonDown(2)
    }
}
