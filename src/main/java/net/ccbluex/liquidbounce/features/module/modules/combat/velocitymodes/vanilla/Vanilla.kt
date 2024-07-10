/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.vanilla

import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Vanilla : VelocityMode("Vanilla") {
    override fun onVelocityPacket(event: PacketEvent) {
        when (event.packet) {
            is S12PacketEntityVelocity -> event.cancelEvent()
            is S27PacketExplosion -> {
                event.packet.field_149152_f = 0f
                event.packet.field_149153_g = 0f
                event.packet.field_149159_h = 0f
            }
        }
    }
}
