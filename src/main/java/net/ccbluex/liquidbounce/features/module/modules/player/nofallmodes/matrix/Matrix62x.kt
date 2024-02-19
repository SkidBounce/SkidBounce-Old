/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.matrix

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.extensions.stop
import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author SkidderMC/FDPClient
 */
object Matrix62x : NoFallMode("Matrix6.2.X") {
    private var spoof = false
    private var falling = false
    private var fallTicks = 0
    private var lastY = 0.0

    override fun onEnable() {
        spoof = false
        falling = false
        fallTicks = 0
        lastY = 0.0
    }

    override fun onUpdate() {
        if (falling) {
            mc.thePlayer.stopXZ()
            mc.thePlayer.jumpMovementFactor = 0f
        }
        if (mc.thePlayer.onGround)
            falling = false

        if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3) {
            if (fallTicks == 0)
                lastY = mc.thePlayer.motionY
            mc.thePlayer.stop()
            mc.thePlayer.jumpMovementFactor = 0f
            mc.thePlayer.fallDistance = 3.2f
            if (fallTicks in 8..9)
                spoof = true
            fallTicks++
        }
        if (fallTicks > 12 && !mc.thePlayer.onGround) {
            mc.thePlayer.motionY = lastY
            mc.thePlayer.fallDistance = 0f
            fallTicks = 0
            spoof = false
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && spoof)
            event.packet.onGround = true
            spoof = false
    }
}
