/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C03PacketPlayer


object HypSpoof : NoFallMode("HypSpoof") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer)
            sendPacket(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y, event.packet.z, true), false)
    }
}
