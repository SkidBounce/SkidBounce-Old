/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.vanilla

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Vanilla : VelocityMode("Vanilla") {
    override fun onVelocityPacket(event: PacketEvent) {
        event.cancelEvent()
    }
}