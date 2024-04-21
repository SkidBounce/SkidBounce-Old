/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla

import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author SkidderMC/FDPClient
 */
object Damage : NoFallMode("Damage") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && mc.thePlayer != null && mc.thePlayer.fallDistance > 3.5) {
            event.packet.onGround = true
        }
    }
}
