package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object Medusa : NoFallMode("Medusa") {
    private var needSpoof = false
    override fun onEnable() {
        needSpoof = false
    }

    override fun onUpdate() {
        if (mc.thePlayer.fallDistance > 2.5) {
            needSpoof = true
            mc.thePlayer.fallDistance = 0f
        }
    }
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && needSpoof) {
            event.packet.onGround = true
            needSpoof = false
        }
    }
}