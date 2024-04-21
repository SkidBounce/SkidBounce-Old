/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.matrix

import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author Zerolysimin#6403
 * @author SkidderMC/FDPClient
 */
object MatrixNew : NoFallMode("MatrixNew") {

    override fun onPacket(event: PacketEvent) {
        if (event.packet !is C03PacketPlayer)
            return

        if (!mc.thePlayer.onGround) {
            if (mc.thePlayer.fallDistance > 2.69f) {
                mc.timer.timerSpeed = 0.3f
                event.packet.onGround = true
                mc.thePlayer.fallDistance = 0f
            }

            mc.timer.timerSpeed = if (mc.thePlayer.fallDistance > 3.5) 0.3f else 1f
        }

        if (mc.theWorld.getCollidingBoundingBoxes(
                mc.thePlayer,
                mc.thePlayer.entityBoundingBox.offset(0.0, mc.thePlayer.motionY, 0.0)
            ).isEmpty()
        ) return

        if (!event.packet.isOnGround && mc.thePlayer.motionY < -0.6)
            event.packet.onGround = true
    }
}
