/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author CCBlueX/LiquidBounce
 */
object Packet2 : CriticalsMode("Packet2") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.0625, true)
        sendPacket(0.0, false)
        sendPacket(1.1E-5, false)
        sendPacket(0.0, false)
        crit(entity)
    }
}
