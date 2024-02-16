/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.matrix

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

object Matrix663 : NoFallMode("Matrix6.6.3") {
    private var packet = false
    private var timer = false

    override fun onEnable() {
        packet = false
    }

    override fun onUpdate() {
        if (timer) {
            mc.timer.resetSpeed()
            timer = false
        }

        if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3F) {
            mc.thePlayer.fallDistance = 0.0f
            packet = true
            mc.timer.timerSpeed = 0.5f
            timer = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (packet && event.packet is C03PacketPlayer) {
            packet = false
            event.cancelEvent()
            sendPacket(
                C04PacketPlayerPosition(
                    event.packet.x,
                    event.packet.y,
                    event.packet.z,
                    true
                ),
                false
            )
            sendPacket(
                C04PacketPlayerPosition(
                    event.packet.x,
                    event.packet.y,
                    event.packet.z,
                    false
                ),
                false
            )
        }
    }
}
