/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.NoFall.spoofgroundAlways
import net.ccbluex.liquidbounce.features.module.modules.player.NoFall.spoofgroundMinFallDistance
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object SpoofGround : NoFallMode("SpoofGround") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && (spoofgroundAlways || mc.thePlayer.fallDistance > spoofgroundMinFallDistance))
            event.packet.onGround = true
    }
}
