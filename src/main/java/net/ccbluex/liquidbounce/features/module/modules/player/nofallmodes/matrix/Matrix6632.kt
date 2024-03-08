/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.matrix

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.NoFall.matrix6632Safe
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.minecraft.network.play.client.C03PacketPlayer

import kotlin.math.abs

/**
 * @author SkidderMC/FDPClient
 */
object Matrix6632 : NoFallMode("Matrix6.6.3-2") {
    private var send = false
    private var firstNfall = true
    private var nearGround = false

    override fun onEnable() {
        send = false
        firstNfall = true
        nearGround = false
    }

    override fun onUpdate() {
        if (mc.thePlayer.fallDistance - mc.thePlayer.motionY <= 3 && !(abs((collY) - mc.thePlayer.posY) < 3 && mc.thePlayer.fallDistance - mc.thePlayer.motionY > 2)) {
            mc.timer.timerSpeed = 1f
            return
        }

        mc.thePlayer.fallDistance = 0.0f
        send = true

        if (matrix6632Safe) {
            mc.timer.timerSpeed = 0.3f
            mc.thePlayer.motionX *= 0.5
            mc.thePlayer.motionZ *= 0.5
        } else mc.timer.timerSpeed = 0.5f
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && send) {
            send = false
            if (abs((collY) - mc.thePlayer.posY) > 2) {
                event.cancelEvent()
                sendPacket(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        event.packet.x,
                        event.packet.y,
                        event.packet.z,
                        true
                    )
                )
                sendPacket(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        event.packet.x,
                        event.packet.y,
                        event.packet.z,
                        false
                    )
                )
            }
        }
    }

    private val collY // null -> too far to calc or fall pos in void
        get() = FallingPlayer(mc.thePlayer).findCollision(60)?.pos?.y ?: 0
}
