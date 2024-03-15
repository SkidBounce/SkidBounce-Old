/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.vanilla

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.minecraft.network.play.server.S32PacketConfirmTransaction

/**
 * @author EclipsesDev
 * @author CCBlueX/LiquidBounce
 */
object S32 : VelocityMode("S32") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is S32PacketConfirmTransaction)

        if (event.isCancelled)
            return

        event.cancelEvent()
    }
    override fun onVelocityPacket(event: PacketEvent) = event.cancelEvent()
}