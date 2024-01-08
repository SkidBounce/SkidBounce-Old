package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object Packet2 : NoFallMode("Packet2") {
    private var lastFallDistRounded = 0
    private var modify = false
    override fun onEnable() {
        lastFallDistRounded = 0
        modify = false
    }

    override fun onUpdate() {
        if (mc.thePlayer.fallDistance.toInt() / 3 > lastFallDistRounded) {
            lastFallDistRounded = mc.thePlayer.fallDistance.toInt() / 3
            modify = true
        }
        if (mc.thePlayer.onGround) {
            lastFallDistRounded = 0
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && modify) {
            event.packet.onGround = true
            modify = false
        }
    }
}