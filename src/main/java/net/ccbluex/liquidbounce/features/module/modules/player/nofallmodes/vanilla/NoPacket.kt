package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object NoPacket : NoFallMode("NoPacket") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer) {
            if (mc.thePlayer != null && mc.thePlayer.fallDistance > 2 && mc.thePlayer.ticksExisted % 2 == 0)
                event.packet.onGround = true
                event.packet.isMoving = false
        }
    }
}