/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.minecraft.network.play.client.C0FPacketConfirmTransaction

/**
 * @author SkidderMC/FDPClient
 */
object Vulcan : VelocityMode("Vulcan") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C0FPacketConfirmTransaction && event.packet.uid <= -30769 && event.packet.uid >= -31767)
            event.cancelEvent()
    }

    override fun onVelocityPacket(event: PacketEvent) {
        event.cancelEvent()
    }
}
