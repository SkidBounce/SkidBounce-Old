/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

object Visual : CriticalsMode("Visual") {
    override fun onAttack(entity: Entity) {
        mc.thePlayer.onCriticalHit(entity)
    }
}
