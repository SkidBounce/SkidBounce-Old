/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other

import net.ccbluex.liquidbounce.event.EventState.POST
import net.ccbluex.liquidbounce.event.EventState.PRE
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverSlot
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

/**
 * @author SkidderMC/FDPClient
 */
object OldIntave : NoSlowMode("OldIntave") {
    override fun onMotion(event: MotionEvent) {
        when (event.eventState) {
            PRE -> {
                serverSlot = serverSlot % 8 + 1
                serverSlot = mc.thePlayer.inventory.currentItem
            }

            POST -> sendPacket(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
        }
    }
}
