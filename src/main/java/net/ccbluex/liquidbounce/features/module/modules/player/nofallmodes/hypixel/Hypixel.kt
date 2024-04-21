/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.hypixel

import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author CCBlueX/LiquidBounce
 */
object Hypixel : NoFallMode("Hypixel") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && mc.thePlayer.fallDistance > 1.5)
            event.packet.onGround = mc.thePlayer.ticksExisted % 2 == 0
    }
}
