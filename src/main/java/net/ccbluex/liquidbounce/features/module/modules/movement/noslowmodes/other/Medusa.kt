/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING

object Medusa : NoSlowMode("Medusa") {
    var sendPacket = true
    override fun onPacket(event: PacketEvent) {
        if ((mc.thePlayer.isUsingItem || mc.thePlayer.isBlocking) && sendPacket) {
            sendPacket(C0BPacketEntityAction(mc.thePlayer, STOP_SPRINTING), false)
            sendPacket = false
        }
        if (!mc.thePlayer.isUsingItem || !mc.thePlayer.isBlocking)
            sendPacket = true
    }
}
