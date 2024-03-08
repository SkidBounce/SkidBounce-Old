/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.hypixel

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author SkidderMC/FDPClient
 */
object HypixelBlink : NoFallMode("HypixelBlink") {
    private var enabled = false
    private var wasOnGround = false
    private val packets = mutableListOf<Packet<*>>()

    override fun onEnable() {
        enabled = false
    }

    override fun onUpdate() {
        if (mc.thePlayer.onGround) wasOnGround = true
        else if (wasOnGround) {
            wasOnGround = false
            if (mc.thePlayer.motionY < 0)
                enabled = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (!enabled || event.eventType != EventState.SEND)
            return

        packets += packet
        event.cancelEvent()

        if (packet is C03PacketPlayer)
            packet.onGround = true

        if (mc.thePlayer.onGround) {
            enabled = false
            sendPackets(*packets.toTypedArray())
            packets.clear()
        }
    }
}
