/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 *  https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vulcan

import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer
import net.ccbluex.liquidbounce.event.PacketEvent

/**
 * @author SkidderMC/FDPClient
 */
object OldVulcan : NoFallMode("OldVulcan") {
    private var doSpoof = false

    override fun onEnable() {
        doSpoof = false
    }

    override fun onUpdate() {
        if (mc.thePlayer.fallDistance > 2.0)
            mc.timer.timerSpeed = 0.9f

        if (mc.thePlayer.onGround)
            mc.timer.timerSpeed = 1f

        if (mc.thePlayer.fallDistance > 2.8) {
            doSpoof = true
            mc.thePlayer.motionY = -0.1
            mc.thePlayer.fallDistance = 0f
            mc.thePlayer.motionY += mc.thePlayer.motionY / 10.0
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && doSpoof) {
            event.packet.onGround = true
            doSpoof = false
        }
    }
}
