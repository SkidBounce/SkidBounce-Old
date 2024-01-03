package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object Verus : NoFallMode("Verus") {
    private var needSpoof = false
    private var packetModify = false
    private var packetCount = 0
    override fun onEnable() {
        needSpoof = false
        packetModify = false
        packetCount = 0
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && needSpoof) {
            event.packet.onGround = true
            needSpoof = false
        }
    }

    override fun onUpdate() {
        if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3) {
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.fallDistance = 0.0f
            mc.thePlayer.motionX *= 0.6
            mc.thePlayer.motionZ *= 0.6
            needSpoof = true
        }

        if (mc.thePlayer.fallDistance.toInt() / 3 > packetCount) {
            packetCount = mc.thePlayer.fallDistance.toInt() / 3
            packetModify = true
        }
        if (mc.thePlayer.onGround) {
            packetCount = 0
        }
    }
}