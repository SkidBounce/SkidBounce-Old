/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.vanilla

import net.ccbluex.liquidbounce.event.events.GameLoopEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.events.WorldEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.spoofDelay
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.ccbluex.liquidbounce.utils.PacketUtils.queuedPackets
import net.minecraft.network.Packet
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S32PacketConfirmTransaction
import java.util.LinkedHashMap

object Delayed : VelocityMode("Delayed") {
    private val packets = LinkedHashMap<Packet<*>, Long>()

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (!event.isCancelled) {
            if (packet is S32PacketConfirmTransaction || packet is S12PacketEntityVelocity) {

                event.cancelEvent()

                // Delaying packet like PingSpoof
                synchronized(packets) {
                    packets[packet] = System.currentTimeMillis()
                }
            }
        }
    }

    override fun onDisable() {
        sendPacketsByOrder(true)

        packets.clear()
    }

    override fun onGameLoop(event: GameLoopEvent) {
        sendPacketsByOrder(false)
    }

    override fun onWorld(event: WorldEvent) {
        packets.clear()
    }

    private fun sendPacketsByOrder(velocity: Boolean) {
        synchronized(packets) {
            packets.entries.removeAll { (packet, timestamp) ->
                if (velocity || timestamp <= (System.currentTimeMillis() - spoofDelay)) {
                    queuedPackets.add(packet)
                    true
                } else false
            }
        }
    }
}
