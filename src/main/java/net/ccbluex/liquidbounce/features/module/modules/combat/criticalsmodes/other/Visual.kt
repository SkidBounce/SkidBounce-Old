package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

object Visual : CriticalsMode("Visual") {
    override fun onAttack(entity: Entity) {
        mc.thePlayer.onCriticalHit(entity)
    }
}
