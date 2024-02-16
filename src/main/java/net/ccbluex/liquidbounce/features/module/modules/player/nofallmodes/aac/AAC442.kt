/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.server.S08PacketPlayerPosLook

/**
 * @author Aspw-w/NightX-Client
 */
object AAC442 : NoFallMode("AAC4.4.2") {
    private var isDmgFalling = false
    private var flags = 0
    private val flagCooldown = MSTimer()
    private var flagWait = 0
    private var usedTimer = false

    override fun onEnable() {
        isDmgFalling = false
        flags = 0
        flagWait = 0
        flagCooldown.reset()
        usedTimer = false
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        usedTimer = false
    }

    override fun onUpdate() {
        if (usedTimer) {
            mc.timer.timerSpeed = 1f
            usedTimer = false
        }
        if (flagWait > 0) {
            if (flagWait-- == 0)
                mc.timer.timerSpeed = 1f
        }
        if (mc.thePlayer.fallDistance > 3)
            isDmgFalling = true
        if (flags >= 3 || flagCooldown.hasTimePassed(1500))
            return
        if (mc.thePlayer.onGround || mc.thePlayer.fallDistance < 0.5) {
            mc.thePlayer.stopXZ()
            mc.thePlayer.onGround = false
            mc.thePlayer.jumpMovementFactor = 0.0f
        }
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S08PacketPlayerPosLook) {
            flags++
            if (flagWait > 0) {
                flagCooldown.reset()
                flags = 1
                event.cancelEvent()
            }
        }
        if (packet is C03PacketPlayer) {
            if (isDmgFalling && packet.onGround && mc.thePlayer.onGround) {
                flagWait = 2
                isDmgFalling = false
                event.cancelEvent()
                mc.thePlayer.onGround = false
                sendPackets(
                    C04PacketPlayerPosition(
                        packet.x,
                        packet.y - 256,
                        packet.z,
                        false
                    ),
                    C04PacketPlayerPosition(
                        packet.x,
                        -10.0,
                        packet.z,
                        true
                    )
                )
                mc.timer.timerSpeed = 0.18f
                usedTimer = true
            }
        }
    }
}
