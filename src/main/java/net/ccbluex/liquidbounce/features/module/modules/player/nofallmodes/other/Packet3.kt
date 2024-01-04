package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object Packet3 : NoFallMode("Packet3") {
    private var packetCount = 0
    private var packetModify = false
    override fun onEnable() {
        packetCount = 0
        packetModify = false
    }

    override fun onUpdate() {
        if (mc.thePlayer.fallDistance.toInt() / 2 > packetCount) {
            packetCount = mc.thePlayer.fallDistance.toInt() / 2
            packetModify = true
        }
        if (mc.thePlayer.onGround) {
            packetCount = 0
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && packetModify) {
            event.packet.onGround = true
            packetModify = false
        }
    }
}