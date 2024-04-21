/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MISC
import net.ccbluex.liquidbounce.utils.ClientUtils.displayClientMessage
import net.ccbluex.liquidbounce.value.BooleanValue
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.server.S2EPacketCloseWindow

object NoInvClose : Module("NoInvClose", MISC, gameDetecting = false) {
    private val notify by BooleanValue("Notify", true) // TODO: translations
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S2EPacketCloseWindow && mc.currentScreen is GuiInventory) {
            event.cancelEvent()
            if (notify) displayClientMessage("§cThe server attempted to close your inventory.")
        }
    }
}
