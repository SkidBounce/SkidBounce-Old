/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author Aspw-w/NightX-Client
 */
object Packet3 : CriticalsMode("Packet3") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.0625, true)
        sendPacket(0.09858, false)
        sendPacket(0.04114514, false)
        sendPacket(0.025, false)
        crit(entity)
    }
}
