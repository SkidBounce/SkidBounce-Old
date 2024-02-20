/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.matrix

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author Zerolysimin#6403
 * @author SkidderMC/FDPClient
 */
object MatrixCollide : NoFallMode("MatrixCollide") {
    private var spoof = false
    override fun onEnable() {
        spoof = false
    }

    override fun onUpdate() {
        if (mc.thePlayer.fallDistance.toInt() - mc.thePlayer.motionY > 3) {
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.fallDistance = 0f
            mc.thePlayer.motionX *= 0.1
            mc.thePlayer.motionZ *= 0.1
            spoof = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && spoof) {
            event.packet.onGround = true
            spoof = false
        }
    }
}
