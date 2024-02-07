/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp

import net.ccbluex.liquidbounce.event.EventState.POST
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos.ORIGIN

object UNCP2 : NoSlowMode("UNCP2") {
    override fun onMotion(event: MotionEvent) {
        if (event.eventState == POST)
            sendPacket(C08PacketPlayerBlockPlacement(ORIGIN, 255, mc.thePlayer.heldItem, 0f, 0f, 0f))
    }
}
