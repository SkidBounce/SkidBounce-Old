/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.NoFall.verusMulti
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object Verus : NoFallMode("Verus") {
    private var spoof = false
    override fun onEnable() {
        spoof = false
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && spoof) {
            event.packet.onGround = true
            spoof = false
        }
    }

    override fun onUpdate() {
        if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3) {
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.fallDistance = 0.0f
            mc.thePlayer.motionX *= verusMulti
            mc.thePlayer.motionZ *= verusMulti
            spoof = true
        }
    }
}
