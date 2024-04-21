/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other

import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow.packetTiming
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 * @author SkidderMC/FDPClient
 */
object EmptyPlace : NoSlowMode("EmptyPlace") {
    override fun onMotion(event: MotionEvent) {
        if (packetTiming(event.eventState))
            sendPacket(C08PacketPlayerBlockPlacement())
    }
}
