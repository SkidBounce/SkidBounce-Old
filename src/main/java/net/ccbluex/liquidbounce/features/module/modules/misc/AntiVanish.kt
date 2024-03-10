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
import net.minecraft.network.play.server.S1DPacketEntityEffect
import net.minecraft.network.play.server.S14PacketEntity

object AntiVanish : Module("AntiVanish", MISC, subjective = true, gameDetecting = false) {
    @EventTarget
    fun onPacket(event: PacketEvent){
        mc.thePlayer ?: return
        mc.theWorld ?: return
        when (event.packet) {
            is S1DPacketEntityEffect, -> check(event.packet.entityId)
            is S14PacketEntity -> check(event.packet.entityId)
        }
    }

    private fun check(entityId: Int) =
        mc.theWorld.getEntityByID(entityId) ?: displayClientMessage("§cDetected Vanished Entity")
}
