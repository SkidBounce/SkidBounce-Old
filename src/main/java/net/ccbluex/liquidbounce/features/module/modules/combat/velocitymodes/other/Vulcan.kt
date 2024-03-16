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
 * @author EclipsesDev
 * @author CCBlueX/LiquidBounce
 */
object Vulcan : VelocityMode("Vulcan") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C0FPacketConfirmTransaction) {

            // prevent for vulcan transaction timeout
            if (event.isCancelled)
                return

            event.cancelEvent()
        }
    }

    override fun onVelocityPacket(event: PacketEvent) = event.cancelEvent()
}
