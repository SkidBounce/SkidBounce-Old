/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.matrix

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook

/**
 * @author SkidderMC/FDPClient
 */
object OldMatrix : NoFallMode("OldMatrix") {
    private var falling = false
    private var flagWait = 0

    override fun onEnable() {
        falling = false
        flagWait = 0
    }

    override fun onUpdate() {
        if (mc.thePlayer.fallDistance > 3) {
            falling = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is S08PacketPlayerPosLook && flagWait > 0) {
            flagWait = 0
            mc.timer.timerSpeed = 1f
            event.cancelEvent()
        }
        if (event.packet is C03PacketPlayer && falling && event.packet.onGround && mc.thePlayer.onGround) {
            flagWait = 2
            falling = false
            event.cancelEvent()
            mc.thePlayer.onGround = false
            sendPacket(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y - 256, event.packet.z, false), false)
            sendPacket(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, (-10).toDouble() , event.packet.z, true), false)
            mc.timer.timerSpeed = 0.18f
        }
    }
}
