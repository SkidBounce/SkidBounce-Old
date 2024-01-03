package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.minecraft.network.play.client.C03PacketPlayer

object HypixelFlag : NoFallMode("HypixelFlag") {
    private var isDmgFalling = false
    override fun onUpdate() {
        if (mc.thePlayer.fallDistance > 3) {
            isDmgFalling = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && isDmgFalling && event.packet.onGround && mc.thePlayer.onGround) {
            isDmgFalling = false
            event.cancelEvent()
            sendPackets(
                C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y, event.packet.z, false),
                C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y, event.packet.z - 23, true),
                C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y, event.packet.z, false),
                triggerEvents = false
            )
        }
    }
}