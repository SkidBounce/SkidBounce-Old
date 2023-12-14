/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange

object OldIntave : NoSlowMode("OldIntave") {
    override fun onMotion(event: MotionEvent) {
        when (event.eventState) {
            EventState.PRE -> {
                PacketUtils.sendPacket(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1))
                PacketUtils.sendPacket(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            }
            EventState.POST -> {
                PacketUtils.sendPacket(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
            }
            else -> {}
        }
    }
}