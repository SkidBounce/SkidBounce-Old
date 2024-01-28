/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.minecraft.network.play.client.C0FPacketConfirmTransaction

object Vulcan : VelocityMode("Vulcan") {
    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C0FPacketConfirmTransaction) {
            val uid = (packet.uid).toInt()
            if (uid >= -31767 && uid <= -30769) {
                event.cancelEvent()
            }
        }
    }

    override fun onVelocityPacket(event: PacketEvent) {
        event.cancelEvent()
    }
}
