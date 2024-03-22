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
object NonCalculable : CriticalsMode("NonCalculable") {
    override fun onAttack(entity: Entity) {
        sendPacket(1E-5, false)
        sendPacket(1E-7, false)
        sendPacket(1E-6, false)
        sendPacket(1E-4, false)
        crit(entity)
    }
}
