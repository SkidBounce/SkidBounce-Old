package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

object Hop : CriticalsMode("Hop") {
    override fun onAttack(entity: Entity) {
        mc.thePlayer.motionY = 0.1
        mc.thePlayer.fallDistance = 0.1f
        mc.thePlayer.onGround = false
    }
}
