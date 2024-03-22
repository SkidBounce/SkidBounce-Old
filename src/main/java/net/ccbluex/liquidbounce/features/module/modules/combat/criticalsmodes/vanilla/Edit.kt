/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.vanilla

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author Aspw-w/NightX-Client
 */
object Edit : CriticalsMode("Edit") {
    private var attacked = false

    override fun onAttack(entity: Entity) { attacked = true }
    override fun onPacket(event: PacketEvent) {
        if (attacked) {
            if (event.packet is C03PacketPlayer)
                event.packet.onGround = false
            attacked = false
        }
    }
}
