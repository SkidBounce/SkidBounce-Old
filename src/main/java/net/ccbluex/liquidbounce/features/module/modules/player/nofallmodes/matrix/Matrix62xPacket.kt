/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.matrix

import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author SkidderMC/FDPClient
 */
object Matrix62xPacket : NoFallMode("Matrix6.2.X-Packet") {
    override fun onUpdate() {
        if (mc.thePlayer.onGround) {
            mc.timer.resetSpeed()
        } else if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3f) {
            mc.timer.timerSpeed =
                (mc.timer.timerSpeed * if (mc.timer.timerSpeed < 0.6) 0.25f else 0.5f).coerceAtLeast(0.2f)
            sendPackets(
                C03PacketPlayer(false),
                C03PacketPlayer(false),
                C03PacketPlayer(false),
                C03PacketPlayer(false),
                C03PacketPlayer(false),
                C03PacketPlayer(true),
                C03PacketPlayer(false),
                C03PacketPlayer(false)
            )
            mc.thePlayer.fallDistance = 0f
        }
    }
}
